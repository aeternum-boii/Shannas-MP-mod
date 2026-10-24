package net.aeternum.shannaMod.parsing;

import net.aeternum.ironj.semanticParser.CheckedPath;
import net.aeternum.ironj.semanticParser.SemanticParser;
import net.aeternum.ironj.semanticParser.dto.NodeDTO;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EventModifierParser {
    public static EventModifier @NotNull [] parse(@NotNull List<Path> files) {
        SemanticParser parser = new SemanticParser();
        files.stream().map(path -> new CheckedPath(new HashSet<>(), path)).forEach(parser::addFiles);
        SemanticParser.ReturnValue rv = parser.parse();

        if(!rv.errors().isEmpty()) System.err.println(rv.errors());

        List<EventModifier> eventModifiers = new ArrayList<>();

        for (List<NodeDTO> file : rv.files().values()) {
            for (NodeDTO node : file) {
                String name = node.key();
                List<Modifier> modifiers = new ArrayList<>();

                node.attributes().forEach(
                        (k,v) -> {
                            String s = v.single();
                            try {
                                modifiers.add(new Modifier(k, Double.parseDouble(s), false));
                            } catch (NumberFormatException e) {
                                modifiers.add(new Modifier(k, s, true));
                            }
                        }
                );

                eventModifiers.add(new EventModifier(name, modifiers));
            }
        }

        return eventModifiers.toArray(new EventModifier[0]);
    }
}
