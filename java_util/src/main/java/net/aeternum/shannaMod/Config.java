package net.aeternum.shannaMod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public record Config(
        String indent,
        Path commonPath,
        int maxBitWidth,
        Charset charset
) {
    public static final Config DEFAULT;
    public static final Charset DEFAULT_CHARSET = Charset.forName("windows-1252");
    static {
        Config cfg;
        try {
            ObjectMapper mapper = new ObjectMapper();
            cfg = mapper.readValue(Path.of("config", "config.json").toFile(), Config.class);
        } catch (IOException e) {
            cfg = new Config("\t", Path.of("..", "mod", "common"), 6, DEFAULT_CHARSET);
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

    public @NonNull String indent(int i) {
        return indent.repeat(i);
    }

    @Contract(pure = true)
    public @NonNull Path eventModifiersPath() {
        return commonPath.resolve("event_modifiers");
    }

    @Contract(pure = true)
    public @NonNull Path scriptedEffectsPath() {
        return commonPath.resolve("scripted_effects");
    }

    @Contract(pure = true)
    public @NonNull Path logicPath() {
        return scriptedEffectsPath();
    }

    public void checkBitWidth(int bitwidth) {
        if(bitwidth > maxBitWidth) System.err.println("Used Bitwidth exceeds recommended maxBitWidth.");
    }
    public void checkBitWidth(int bitwidth, Class<?> clazz) {
        if(bitwidth > maxBitWidth) System.err.println(clazz + ": Used Bitwidth exceeds recommended maxBitWidth.");
    }

    public int clampBitWidth(int bitwidth) {
        return Math.min(bitwidth, maxBitWidth);
    }
}
