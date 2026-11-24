package net.aeternum.eu4MapUtil.parser;

import net.aeternum.eu4MapUtil.map.Province;
import net.aeternum.ironj.util.math.vec.Vec3i;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefinitionParser {
    private final Path gamePath;
    private static final Path DEFINITION_PATH = Path.of("map", "definition.csv");
    public List<Province> provinces = new ArrayList<>();

    public DefinitionParser(@NonNull Path gamePath) {
        this.gamePath = gamePath;
    }

    public DefinitionParser parse() {
        try {
            String file = Files.readString(gamePath.resolve(DEFINITION_PATH), Charset.forName("windows-1252"));
            String[] lines = file.split("\n");
            for (int i = 1; i < lines.length; i++){
                provinces.add(parseProvince(lines[i]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Contract("_ -> new")
    private @NonNull Province parseProvince(@NonNull String line) {
        String[] fields = line.split(";");
        try {
            return new Province(
                    Integer.parseInt(fields[0]),
                    Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]),
                    fields[4]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("Invalid Province in definition.csv\n" + e.getMessage());
        }
    }

    public HashMap<Vec3i, Province> asRGBLookup() {
        HashMap<Vec3i, Province> map = new HashMap<>();
        for (Province province : provinces) {
            map.put(province.rgb, province);
        }
        return map;
    }

}
