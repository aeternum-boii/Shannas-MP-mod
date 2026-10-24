package net.aeternum.shannaMod;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.aeternum.shannaMod.eliteGuards.EliteGuardsBuffMultiplierBuilder;
import net.aeternum.shannaMod.eliteGuards.EliteGuardsModifierGenerator;
import net.aeternum.shannaMod.parsing.EventModifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() throws IOException {
        Path p = Path.of("..\\mod\\common");

        EliteGuardsBuffMultiplierBuilder builder = new EliteGuardsBuffMultiplierBuilder(p, 8);
        List<EventModifier> modified = builder.generateScaledModifiers();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(Files.newBufferedWriter(Path.of("out.json")), modified);

        for(EventModifier mod : modified) {
            System.out.println(mod.toString());
        }
    }
}
