package net.aeternum.shannaMod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.nio.file.Path;

public record Config(
        String indent
) {
    public static final Config DEFAULT;
    static {
        Config cfg;
        try {
            ObjectMapper mapper = new ObjectMapper();
            cfg = mapper.readValue(Path.of("config", "config.json").toFile(), Config.class);
        } catch (IOException e) {
            cfg = new Config("\t");
        }
        DEFAULT = cfg;
    }

    public static void generateDefaultConfig() {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(Path.of("config", "config.json").toFile(), DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Config readDefaultConfig() {
        return DEFAULT;
    }

    public String indent(int i) {
        return indent.repeat(i);
    }
}
