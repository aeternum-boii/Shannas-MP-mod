package net.aeternum.shannaMod.parsing;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record EventModifier (
        String name,
        List<Modifier> modifiers
) implements Comparable<EventModifier> {
    @Override
    @NonNull
    public String toString() {
        List<String> modifierStrings = modifiers.stream().map(Modifier::toString).toList();
        StringBuilder sb = new StringBuilder(name).append(" = {\n");
        modifierStrings.forEach(m -> sb.append('\t').append(m).append('\n'));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull EventModifier o) {
        if(isSuffixed() && o.isSuffixed() && o.getSuffixlessName().equals(getSuffixlessName())) {
            return getSuffix().compareTo(o.getSuffix());
        }

        return name.compareTo(o.name());
    }

    // To both limit the length of suffixes and preserve order of negative modifier < 0 < positive modifier.
    public static @NonNull String canonicSuffixNaming(String name, Double suffix) {
        return name + canonicSuffixNaming(suffix);
    }
    private static @NonNull String canonicSuffixNaming(Double suffix) {
        boolean isNegative = suffix < 0;
        if(isNegative) suffix = -suffix;
        String suffixString = String.valueOf(suffix);
        int cutoffPoint = 1;
        int commaIndex = suffixString.indexOf('.');

        for(; commaIndex + cutoffPoint < suffixString.length(); cutoffPoint++) {
            if(suffixString.charAt(commaIndex + cutoffPoint) == '0' || cutoffPoint >= 3) break;
        }

        suffixString = suffixString.substring(0, commaIndex + cutoffPoint);
        if(suffixString.endsWith(".")) suffixString += "0";
        suffixString += isNegative ? "_n" : "_p";

        return "_" + suffixString.replace('.', '_');
    }

    public boolean isSuffixed() {
        return name.endsWith("_n") || name.endsWith("_p");
    }

    public Double getSuffix() {
        if(!isSuffixed()) throw new IllegalStateException("The suffix has not been set");
        int sign = name.endsWith("_n") ? -1 : 1;
        String suffix = "";
        String name = this.name();
        name = name.substring(0, name.length() - 2);

        boolean foundDelimiter = false;
        for(int i = name.length() - 1; i >= 0; i--) {
            if(name.charAt(i) == '_') {
                if(foundDelimiter) break;
                suffix = '.' + suffix;
                foundDelimiter = true;
            } else {
                suffix = name.charAt(i) + suffix;
            }
        }


        return Double.parseDouble(suffix) * sign;
    }

    public String getSuffixlessName() {
        if(!isSuffixed()) throw new IllegalStateException("The suffix has not been set");
        String suffix = canonicSuffixNaming(getSuffix());
        return name.substring(0, name.length() - suffix.length());
    }
}
