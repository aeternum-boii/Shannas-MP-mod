package net.aeternum.eu4MapUtil.map;

import net.aeternum.eu4MapUtil.parser.DefinitionParser;
import net.aeternum.ironj.util.math.vec.Vec3i;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Mapdef {
    private final Map<Vec3i, Province> colorMap;
    private final Map<Integer, Province> idMap = new HashMap<>();

    private Stack<Runnable> history = new Stack<>();

    public Mapdef(Path gamePath) {
        DefinitionParser parser = new DefinitionParser(gamePath);
        parser.parse();

        colorMap = parser.asRGBLookup();
        for (Province province : colorMap.values()) {
            idMap.put(province.provinceId, province);
        }
        verifyState();
    }

    public void deleteProvince(@NonNull Province province) {
        idMap.remove(province.provinceId);
        colorMap.remove(province.rgb);

        history.push(() -> addProvince(province));
    }

    public void deleteProvince(int provinceId) {
        Province province = idMap.get(provinceId);
        deleteProvince(province);
    }

    public Province getProvince(int provinceId) {
        return idMap.get(provinceId);
    }

    public Province getProvince(Vec3i rgb) {
        return colorMap.get(rgb);
    }

    public void addProvince(Province province) {
        idMap.put(province.provinceId, province);
        colorMap.put(province.rgb, province);

        history.push(() -> deleteProvince(province));
    }

    private void verifyState() {
        if(colorMap.size() != idMap.size()) throw new IllegalStateException();
        for (Province province : colorMap.values()) {
            if (idMap.get(province.provinceId) == null) throw new IllegalStateException("Cannot verify existance of Province " + province);
        }
    }

    public void undoHistory() {
        history.pop().run();
    }

    public void serialise() {
        verifyState();
    }
}
