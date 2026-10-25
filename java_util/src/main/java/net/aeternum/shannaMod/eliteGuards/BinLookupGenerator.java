package net.aeternum.shannaMod.eliteGuards;

import net.aeternum.shannaMod.Config;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BinLookupGenerator {
    private final Config cfg;

    public BinLookupGenerator(Config cfg) {
        this.cfg = cfg;
    }

    public BinLookupGenerator fromDefaultConfig() {
        return new BinLookupGenerator(Config.readDefaultConfig());
    }

    public String generate(@NonNull ValueInjection injection, @NonNull List<Double> values) {
        values.sort(Double::compareTo);
        String genComment = "Automatically generated function for a binary lookup table\n"
                + "Parameters:\n" +  cfg.indent(1) + "Trigger Block:\n" + cfg.indent(1) + injection.genTriggerBlock(values.getFirst(), 2) +
                cfg.indent(1) + "Effect Block: " + injection.genEffectBlock(values.getFirst(), 0) + "\n" + cfg.indent(1) + "Values: [" +
                values.stream().map("%.3f"::formatted).reduce((a, b) -> a + ", " + b).orElse("") + "]";
        String[] genCommentLines = genComment.split("\n");
        for(int i = 0; i < genCommentLines.length; i++) genCommentLines[i] = "# " + genCommentLines[i];
        String function = generate(injection, values, 0);
        StringBuilder sb = new StringBuilder();
        for(String line : genCommentLines) sb.append(line).append('\n');
        sb.append(function);

        return sb.toString();
    }

    private @NonNull String generate(ValueInjection injection, @NonNull List<Double> values, int depth) {
        if(values.isEmpty()) return "";
        if(values.size() == 1) {
            if(injection.hasValueFormatInEffect()) return cfg.indent(depth) + injection.genEffectBlock(values.getFirst(), depth).formatted(values.getFirst());
            else return cfg.indent(depth) + injection.genEffectBlock(values.getFirst(), depth);
        }
        return cfg.indent(depth++) + "if = {\n" + cfg.indent(depth) +
                "limit = {\n" + cfg.indent(depth + 1) + injection.genTriggerBlock(values.get(values.size() / 2), depth + 1) // >= comparison
                + cfg.indent(depth) + "}\n"
                + generate(injection, values.subList(values.size() / 2, values.size()), depth) + cfg.indent(depth - 1) + "}\n" + // >= path
                cfg.indent(depth - 1) +
                "else = {\n" + generate(injection, values.subList(0, values.size() / 2), depth) + cfg.indent(depth - 1) + "}\n";  // < path
    }


    public abstract static class ValueInjection {
        /**
         * @return a String that represents the block that checks for >=
         */
        public abstract String genTriggerBlock(Double value, int depth);

        /**
         * @return A String that represents the block that applies the effect (needs a %.3d for the value)
         */
        public abstract String genEffectBlock(Double value, int depth);

        /**
         * must return false if genEffectBlock().formatted(value) fails
         */
        public boolean hasValueFormatInEffect() {
            return true;
        }
    }
    public abstract static class SimpleValueInjection extends ValueInjection {
        private final Config cfg;

        protected SimpleValueInjection(Config cfg) {
            this.cfg = cfg;
        }

        @Contract(pure = true)
        @Override
        public @NonNull String genTriggerBlock(Double value, int depth) {
            return "check_variable = {\n" + cfg.indent(depth + 1) + "which = " + getVariableName() + "\n" + cfg.indent(depth + 1) + "value = %.3f".formatted(value) + "\n" + cfg.indent(depth) + "}\n";
        }
        protected abstract String getVariableName();

        @Override
        public String genEffectBlock(Double value, int depth) {
            return getEffect() + " = %.3f\n";
        }
        protected abstract String getEffect();
    }
}
