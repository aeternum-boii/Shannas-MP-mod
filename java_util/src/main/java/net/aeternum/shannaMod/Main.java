package net.aeternum.shannaMod;

import net.aeternum.shannaMod.eliteGuards.EliteGuardsBuffMultiplierBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    static void main() throws IOException {
        Path p = Path.of("..","mod","common");

        EliteGuardsBuffMultiplierBuilder builder = new EliteGuardsBuffMultiplierBuilder(p, 6)
                .generateScaledModifiers()
                .writeScaledModifiers()
                .writeLogic();
    }
}
