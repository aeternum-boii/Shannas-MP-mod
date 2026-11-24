package net.aeternum.eu4MapUtil;

import net.aeternum.eu4MapUtil.parser.DefinitionParser;

import java.nio.file.Path;

public class Main {
    static void main() {
        DefinitionParser p = new DefinitionParser(Path.of("..", "mod"))
                .parse();
    }
}
