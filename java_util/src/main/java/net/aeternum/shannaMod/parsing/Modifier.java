package net.aeternum.shannaMod.parsing;

import org.jspecify.annotations.NonNull;

public record Modifier(
        String modifier,
        String value,
        boolean failsDoubleParse
) {
    public Modifier(String modifier, double v) {
        this(modifier, String.format("%.3f", v), false);
    }

    public @NonNull Double parsedValue() {
        return Double.parseDouble(this.value);
    }

    public @NonNull Modifier scale(Double multiplier) {
        if(failsDoubleParse) return new Modifier(modifier, value, true);
        return new Modifier(modifier, parsedValue() * multiplier);
    }

    @Override @NonNull
    public String toString() {
        String value = this.value;
        if(value.equals("true")) {
            value = "yes";
        } else if(value.equals("false")) {
            value = "no";
        }

        return modifier + " = " + value;
    }
}
