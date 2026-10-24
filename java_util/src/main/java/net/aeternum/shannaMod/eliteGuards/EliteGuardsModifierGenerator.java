package net.aeternum.shannaMod.eliteGuards;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.aeternum.shannaMod.parsing.EventModifier;
import net.aeternum.shannaMod.parsing.EventModifierParser;
import net.aeternum.shannaMod.parsing.Modifier;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EliteGuardsModifierGenerator {
    private final HashMap<String, Boolean> modifierClassification;

    public EliteGuardsModifierGenerator(HashMap<String, Boolean> modifierClassification) {
        this.modifierClassification = modifierClassification;
    }

    @Contract("_ -> new")
    public static @NonNull EliteGuardsModifierGenerator fromConfig(@NonNull Path modConfigPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String file = Files.readString(modConfigPath);
            HashMap<String, String[]> classifications =  mapper.readValue(file, new TypeReference<>(){});
            HashMap<String, Boolean> modifierClassification = new HashMap<>();
            for(String positive : classifications.get("positive_modifiers")) modifierClassification.put(positive, true);
            for(String negative : classifications.get("negative_modifiers")) modifierClassification.put(negative, false);

            return new EliteGuardsModifierGenerator(modifierClassification);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static @NonNull EliteGuardsModifierGenerator fromConfig() {
        return fromConfig(Path.of("config", "modifier_classification.json"));
    }

    @NonNull
    public HashMap<Double, List<EventModifier>> generateEliteGuardsModifiers(Path file, List<Double> multipliers) {
        try {
            String fileStr = Files.readString(file);
            ObjectMapper mapper = new ObjectMapper();
            EventModifier[] eventModifiers = mapper.readValue(fileStr, EventModifier[].class);
            return generateEliteGuardsModifiers(eventModifiers, multipliers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @NonNull
    public HashMap<Double, List<EventModifier>> generateEliteGuardsModifiers(EventModifier @NonNull [] modifiers, List<Double> multipliers) {
        HashMap<Double, List<EventModifier>> result = new HashMap<>();
        boolean error = false;

        for (EventModifier modifier : modifiers) {
            List<Modifier> thisModifiers = modifier.modifiers();

            for (Double multiplier : multipliers) {
                List<Modifier> newList = new ArrayList<>(thisModifiers.size());
                for (Modifier thisModifier : thisModifiers) {
                    if(modifierClassification.get(thisModifier.modifier()) == null) {
                        System.out.println(thisModifier.modifier());
                        error = true;
                        continue;
                    }
                    boolean isGreaterZero = !thisModifier.failsDoubleParse() && Double.parseDouble(thisModifier.value()) > 0;
                    if (modifierClassification.get(thisModifier.modifier())) {
                        if(isGreaterZero) newList.add(thisModifier.scale(multiplier));
                        else newList.add(thisModifier.scale(1 / multiplier));
                    } else {
                        if(isGreaterZero) newList.add(thisModifier.scale(1 / multiplier));
                        else newList.add(thisModifier.scale(multiplier));
                    }
                }
                result.putIfAbsent(multiplier, new ArrayList<>(modifiers.length));
                result.get(multiplier).add(new EventModifier(EventModifier.canonicSuffixNaming(modifier.name(), multiplier), newList));
            }
        }

        if(error) throw new RuntimeException();
        return result;
    }

    public @NonNull Map<Double, List<EventModifier>> generateEliteGuardsModifiers(Path file, List<Double> multipliers, boolean isEu4File) {
        if(isEu4File) return generateEliteGuardsModifiers(EventModifierParser.parse(List.of(file)), multipliers);
        return generateEliteGuardsModifiers(file, multipliers);
    }
}
