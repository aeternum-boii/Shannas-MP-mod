package net.aeternum.shannaMod.eliteGuards;

import net.aeternum.shannaMod.Config;
import net.aeternum.shannaMod.generation.BinLookupGenerator;
import net.aeternum.shannaMod.generation.ScaledModifierGenerator;
import net.aeternum.shannaMod.parsing.EventModifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EliteGuardsBuffMultiplierBuilder {
    private final Path commonPath;
    private final Path configPath;
    private final List<Double> modifiers;
    private final HashMap<String, List<EventModifier>> scaledModifiers = new HashMap<>();
    private static final Path scaledModifierSource = Path.of("event_modifiers", "aet_eliteGuards_scaled_modifiers.txt");
    private static final Path scaledModifierTarget = Path.of("event_modifiers", "aet_eliteGuards_scaled_modifiers.generated.txt");
    private static final Path logicTarget = Path.of("scripted_effects", "aet_eliteGuards_scaledModifiers_scripted_effects.generated.txt");
    private static final String buffModifier = "aet_eliteGuards_buffMultiplier";
    private final Config cfg;

    public EliteGuardsBuffMultiplierBuilder(Path commonPath, int bitWidth, double step, Path configPath, Config cfg) {
        this.commonPath = commonPath;
        this.configPath = configPath;

        modifiers = new ArrayList<>(1 << (bitWidth - 1));
        this.cfg = cfg;
        for (int i = 3; i < (1 << (bitWidth - 1)) + 3; i++) {
            modifiers.add(step * i);
        }
    }

    public EliteGuardsBuffMultiplierBuilder(Path commonPath, int bitWidth, double step) {
        this(commonPath, bitWidth, step, null, Config.readDefaultConfig());
    }
    public EliteGuardsBuffMultiplierBuilder(Path commonPath, int bitWidth) {
        this(commonPath, bitWidth, 0.10, null, Config.readDefaultConfig());
    }

    public EliteGuardsBuffMultiplierBuilder generateScaledModifiers() {
        ScaledModifierGenerator generator = configPath == null ? ScaledModifierGenerator.fromConfig() : ScaledModifierGenerator.fromConfig(configPath);
        Map<Double, List<EventModifier>> result = generator.scaleModifiersInFile(commonPath.resolve(scaledModifierSource), modifiers, generator.POSITIVE_SCALES_POSITIVE, true);

        List<EventModifier> eventModifiers = new ArrayList<>(result.values().stream().flatMap(List::stream).toList());
        for (EventModifier modifier : eventModifiers) {
            scaledModifiers.putIfAbsent(modifier.getSuffixlessName(), new ArrayList<>());
            scaledModifiers.get(modifier.getSuffixlessName()).add(modifier);
        }
        for (List<EventModifier> modifierList : scaledModifiers.values()) modifierList.sort(Comparator.comparing(EventModifier::name));

        return this;
    }

    public HashMap<String, List<EventModifier>> getScaledModifiers() {
        return scaledModifiers;
    }

    public EliteGuardsBuffMultiplierBuilder writeScaledModifiers() {
        try(BufferedWriter writer = Files.newBufferedWriter(commonPath.resolve(scaledModifierTarget))) {
            List<EventModifier> combined = new ArrayList<>(scaledModifiers.values().stream().flatMap(List::stream).toList());
            combined.sort(Comparator.comparing(EventModifier::getSuffixlessName));
            for (EventModifier modifier : combined) {
                writer.write(modifier + "\n");
            }
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public EliteGuardsBuffMultiplierBuilder writeLogic() {
        final StringBuilder sb = new StringBuilder();
        final String modVariableName = "aet_eliteGuards_buffMultiplier_var";
        sb.append("aet_eliteGuards_refreshScaledModifiers = {\n");
        sb.append(cfg.indent(1)).append("aet_eliteGuards_removeBuffModifiers = yes\n");
        sb.append(cfg.indent(1)).append("export_to_variable = {\n");
        sb.append(cfg.indent(2)).append("which = ").append(modVariableName).append("\n").append(cfg.indent(2))
                .append("value = modifier:").append(buffModifier).append("\n").append(cfg.indent(1)).append("}\n");

        final BinLookupGenerator gen = new BinLookupGenerator(cfg);

        scaledModifiers.forEach((flagName, modifierLists) -> {
            HashMap<Double, String> modifierNames = new HashMap<>();
            for(EventModifier modifier : modifierLists) modifierNames.put(modifier.getSuffix(), modifier.name());
            String modifierBlock = cfg.indent(1) + "if = {\n" + cfg.indent(2) + "limit = { has_country_flag = " + flagName + " }\n";
            String generatedBlock = gen.generate(new BinLookupGenerator.SimpleValueInjection(cfg) {
                @Override
                protected String getVariableName() {
                    return modVariableName;
                }

                @Override
                public String genEffectBlock(Double value, int depth) {
                    return "add_country_modifier = {\n" + cfg.indent(depth + 1) + "name = " + modifierNames.get(value) + "\n" +
                           cfg.indent(depth + 1) + "duration = -1\n" + cfg.indent(depth) + "}\n";
                }

                @Override
                protected String getEffect() {
                    return "";
                }

                @Override
                public boolean hasValueFormatInEffect() {
                    return false;
                }
            }, modifiers);
            String[] gb = generatedBlock.split("\n");
            for(int i = 0; i < gb.length; i++) gb[i] = cfg.indent(2) + gb[i];
            sb.append(modifierBlock);
            sb.append(String.join("\n", gb));
            sb.append("\n").append(cfg.indent(1)).append("}\n");
        });

        sb.append("}\n");

        sb.append("aet_eliteGuards_removeBuffModifiers = {\n");
        for(List<EventModifier> modifierLists : scaledModifiers.values()) {
            for(EventModifier modifier : modifierLists) {
                sb.append(cfg.indent(1)).append("remove_country_modifier = ").append(modifier.name()).append("\n");
            }
        }
        sb.append("}\n");

        sb.append("aet_eliteGuards_cleanUpBuffFlags = { # Effectively equal to calling aet_eliteGuards_removeCountryModifier on all modifiers\n");
        for(String flagName : scaledModifiers.keySet()) {
            sb.append(cfg.indent(1)).append("clr_country_flag = ").append(flagName).append("\n");
        }
        sb.append("}\n");

        try (BufferedWriter writer = Files.newBufferedWriter(commonPath.resolve(logicTarget))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }


}
