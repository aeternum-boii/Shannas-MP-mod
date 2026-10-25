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

public class ScaledModifierGenerator {
    public final Scalar POSITIVE_SCALES_POSITIVE = new Scalar() {
        @Override
        public @NonNull Modifier scale(@NonNull Modifier modifier, Double multiplier) {
            if(modifierClassification.get(modifier.modifier()) == null) {
                System.err.println(modifier.modifier());
            }
            boolean isGreaterZero = !modifier.failsDoubleParse() && Double.parseDouble(modifier.value()) > 0;
            if (modifierClassification.get(modifier.modifier())) {
                if(isGreaterZero) return (modifier.scale(multiplier));
                else return (modifier.scale(1 / multiplier));
            } else {
                if(isGreaterZero) return (modifier.scale(1 / multiplier));
                else return (modifier.scale(multiplier));
            }
        }

        @Override
        public Modifier scale(Modifier modifier, Double multiplier, boolean[] error) {
            Modifier result = super.scale(modifier, multiplier, error);
            if(modifierClassification.get(modifier.modifier()) == null) error[0] = true;
            return result;
        }
    };
    public static final Scalar SIMPLE = new Scalar() {
        @Override
        public @NonNull Modifier scale(@NonNull Modifier modifier, Double multiplier) {
            return modifier.scale(multiplier);
        }
    };
    public static final Scalar INVERSE = new Scalar() {
        @Override
        public @NonNull Modifier scale(@NonNull Modifier modifier, Double multiplier) {
            return modifier.scale(1 / multiplier);
        }
    };
    private final HashMap<String, Boolean> modifierClassification;

    public ScaledModifierGenerator(HashMap<String, Boolean> modifierClassification) {
        this.modifierClassification = modifierClassification;
    }

    @Contract("_ -> new")
    public static @NonNull ScaledModifierGenerator fromConfig(@NonNull Path modConfigPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String file = Files.readString(modConfigPath);
            HashMap<String, String[]> classifications =  mapper.readValue(file, new TypeReference<>(){});
            HashMap<String, Boolean> modifierClassification = new HashMap<>();
            for(String positive : classifications.get("positive_modifiers")) modifierClassification.put(positive, true);
            for(String negative : classifications.get("negative_modifiers")) modifierClassification.put(negative, false);

            return new ScaledModifierGenerator(modifierClassification);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static @NonNull ScaledModifierGenerator fromConfig() {
        return fromConfig(Path.of("config", "modifier_classification.json"));
    }

    @NonNull
    public HashMap<Double, List<EventModifier>> scaleModifiersInFile(Path file, List<Double> multipliers, Scalar scalar) {
        try {
            String fileStr = Files.readString(file);
            ObjectMapper mapper = new ObjectMapper();
            EventModifier[] eventModifiers = mapper.readValue(fileStr, EventModifier[].class);
            return scaleModifiers(eventModifiers, multipliers, scalar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @NonNull
    public HashMap<Double, List<EventModifier>> scaleModifiers(EventModifier @NonNull [] modifiers, List<Double> multipliers, Scalar scalar) {
        HashMap<Double, List<EventModifier>> result = new HashMap<>();
        boolean[] error = new boolean[1];

        for (EventModifier modifier : modifiers) {
            List<Modifier> thisModifiers = modifier.modifiers();

            for (Double multiplier : multipliers) {
                List<Modifier> newList = new ArrayList<>(thisModifiers.size());
                for (Modifier thisModifier : thisModifiers) {
                    newList.add(scalar.scale(thisModifier, multiplier, error));
                }
                result.putIfAbsent(multiplier, new ArrayList<>(modifiers.length));
                result.get(multiplier).add(new EventModifier(EventModifier.canonicSuffixNaming(modifier.name(), multiplier), newList));
            }
        }

        if(error[0]) throw new RuntimeException();
        return result;
    }

    public @NonNull Map<Double, List<EventModifier>> scaleModifiersInFile(Path file, List<Double> multipliers, Scalar scalar, boolean isEu4File) {
        if(isEu4File) return scaleModifiers(EventModifierParser.parse(List.of(file)), multipliers, scalar);
        return scaleModifiersInFile(file, multipliers, scalar);
    }


    public abstract static class Scalar {
        public abstract Modifier scale(Modifier modifier, Double multiplier);
        public Modifier scale(Modifier modifier, Double multiplier, boolean[] error) {
            if(error == null || error.length == 0) throw new IllegalArgumentException("error array must be non-null and non-empty");
            error[0] = false;
            return scale(modifier, multiplier);
        }
    }
}
