package net.aeternum.shannaMod.eliteGuards;

import net.aeternum.shannaMod.Config;
import net.aeternum.shannaMod.generation.BinLookupGenerator;
import net.aeternum.shannaMod.parsing.EventModifier;
import net.aeternum.shannaMod.parsing.Modifier;

import java.util.*;

public class MercModifierBuilder {
    private final Config cfg;

    public MercModifierBuilder(Config cfg) {
        this.cfg = cfg;
    }

    public MercModifierBuilder fromDefaultConfig() {
        return new MercModifierBuilder(Config.readDefaultConfig());
    }

    static void main() {
        MercModifierBuilder builder = new MercModifierBuilder(Config.readDefaultConfig());
        System.out.println(builder.generate(0.5, 3));
    }

    public String generate(double c, int maxEliteBuffs) {
        return generate(c, maxEliteBuffs, 0.5, 1.5, 5);
    }

    public String generate(double c, int maxEliteBuffs, double minMercModifier, double maxMercModifier, int bucketCount) {
        double[] boundaries = generateBoundaries(c, maxEliteBuffs, minMercModifier, maxMercModifier, bucketCount);
        EventModifier[] modifiers = new EventModifier[boundaries.length];
        Modifier unscaledModifier = new Modifier("land_maintenance_modifier", 1.0);
        Modifier revGuard = new Modifier("is_revolutionary_guard_modifier", "yes", true);
        HashMap<Double, Integer> modifierMapping = new HashMap<>();

        for(int i = 0; i < modifiers.length; i++) {
            modifiers[i] = new EventModifier("aet_eliteGuards_plutoCost_" + i, List.of(revGuard, unscaledModifier.scale(boundaries[i])));
            modifierMapping.put(boundaries[i], i);
        }

        StringBuilder sb = new StringBuilder("# Modifiers: \n");
        for(EventModifier modifier : modifiers) sb.append(modifier).append('\n');

        String varName = "aet_eliteGuardsCombinedModifier";

        BinLookupGenerator gen = new BinLookupGenerator(cfg);
        String logic = gen.generate(
                new BinLookupGenerator.SimpleValueInjection(cfg) {
                    @Override
                    protected String getVariableName() {
                        return varName;
                    }

                    @Override
                    public String genEffectBlock(Double value, int depth) {
                        return "aet_eliteGuards_addCountryModifier = {\n" + cfg.indent(depth + 1) + "name = aet_eliteGuards_plutoCost_"
                                + modifierMapping.get(value) + "\n" + cfg.indent(depth) + "}\n";
                    }

                    @Override
                    public boolean hasValueFormatInEffect() {
                        return false;
                    }

                    @Override
                    protected String getEffect() {
                        return "";
                    }
                }, new ArrayList<>(Arrays.stream(boundaries).boxed().toList())
        );

        sb.append("\n# Logic: \n").append(logic);

        return sb.toString();
    }

    /**
     * Generates {@code bucketCount + 1} boundaries dividing the range
     * {@code [c * minX, c * maxX * maxY]} into {@code bucketCount} buckets
     * whose widths increase linearly.
     *
     * <p>The unscaled bucket widths form an arithmetic progression from
     * {@code 0.4} to {@code 1.0}, after which they are normalised to span
     * the complete range. The scaling factor {@code c} is applied only to
     * the resulting boundaries, since it has no effect on their relative
     * spacing.
     *
     * @param c scaling factor applied to all boundaries
     * @param maxY maximum expected value of {@code y}, with {@code y >= 1}
     * @param minX minimum expected value of {@code x}
     * @param maxX maximum expected value of {@code x}
     * @param bucketCount number of buckets; must be positive
     * @return {@code bucketCount + 1} monotonically increasing boundaries
     * @throws IllegalArgumentException if an argument is invalid
     */
    private static double[] generateBoundaries(
            double c,
            double maxY,
            double minX,
            double maxX,
            int bucketCount) {

        if (!Double.isFinite(c)
                || !Double.isFinite(maxY)
                || !Double.isFinite(minX)
                || !Double.isFinite(maxX)
                || maxY < 1.0
                || minX >= maxX
                || bucketCount <= 0) {
            throw new IllegalArgumentException("Invalid bucket parameters.");
        }

        double upper = maxX * maxY;
        double range = upper - minX;

        double[] boundaries = new double[bucketCount + 1];
        boundaries[0] = minX;

        if (bucketCount == 1) {
            boundaries[1] = upper;
        } else {
            double step = 0.6 / (bucketCount - 1);
            double sum = 0.7 * bucketCount;
            double accumulated = 0.0;

            for (int i = 0; i < bucketCount; i++) {
                accumulated += 0.4 + i * step;
                boundaries[i + 1] = minX + range * accumulated / sum;
            }
        }

        for (int i = 0; i < boundaries.length; i++) {
            boundaries[i] *= c;
        }

        return boundaries;
    }
}
