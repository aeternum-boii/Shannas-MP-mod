package net.aeternum.shannaMod.eliteGuards;

import net.aeternum.shannaMod.parsing.EventModifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EliteGuardsBuffMultiplierBuilder {
    private final Path commonPath;
    private final Path configPath;
    private final List<Double> modifiers;
    private List<EventModifier> scaledModifiers = null;
    private static final Path scaledModifierSource = Path.of("event_modifiers", "aet_eliteGuards_scaled_modifiers.txt");
    private static final Path scaledModifierTarget = Path.of("event_modifiers", "aet_eliteGuards_scaled_modifiers.generated.txt");
    private static final Path logicTarget = Path.of("scripted_effects", "aet_eliteGuards_scaledModifiers_scripted_effects.generated.txt");

    public EliteGuardsBuffMultiplierBuilder(Path commonPath, int bitWidth, double step, Path configPath) {
        this.commonPath = commonPath;
        this.configPath = configPath;

        modifiers = new ArrayList<>(1 << (bitWidth - 1));
        for (int i = -(1 << (bitWidth - 2)); i < (1 << (bitWidth - 2)); i++) {
            modifiers.add(step * i);
        }
    }

    public EliteGuardsBuffMultiplierBuilder(Path commonPath, int bitWidth, double step) {
        this(commonPath, bitWidth, step, null);
    }
    public EliteGuardsBuffMultiplierBuilder(Path commonPath, int bitWidth) {
        this(commonPath, bitWidth, 0.10, null);
    }

    public List<EventModifier> generateScaledModifiers() {
        EliteGuardsModifierGenerator generator = configPath == null ? EliteGuardsModifierGenerator.fromConfig() : EliteGuardsModifierGenerator.fromConfig(configPath);
        Map<Double, List<EventModifier>> result = generator.generateEliteGuardsModifiers(commonPath.resolve(scaledModifierSource), modifiers, true);

        scaledModifiers = new ArrayList<>(result.values().stream().flatMap(List::stream).toList());
        scaledModifiers.sort(EventModifier::compareTo);

        return scaledModifiers;
    }


}
