package net.aeternum.shannaMod;

import net.aeternum.shannaMod.building.FLBuildings;
import net.aeternum.shannaMod.building.eliteGuards.EliteGuardsBuffMultiplierBuilder;
import net.aeternum.shannaMod.parsing.FileSorter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class Main {
    static void main() {
//        EliteGuardsBuffMultiplierBuilder egbmb = new EliteGuardsBuffMultiplierBuilder(5)
//                .generateScaledModifiers()
//                .writeScaledModifiers()
//                .writeLogic()
//                .writeLocalisation();

        FLBuildings flb = FLBuildings.fromDefaultConfig()
                .setBuildingModifier(new String[]{"regimental_camp", "conscription_center"}, "land_forcelimit")
                .buildModifiers(4, 0.05, 5)
                .writeModifiers()
                .writeLogic()
                .setBuildingModifier(new String[]{"shipyard", "grand_shipyard"}, "naval_forcelimit")
                .buildModifiers(4, 0.10, 5)
                .writeModifiers()
                .writeLogic();

        Path p = Path.of("..", "mod", "common", "ideas", "00_basic_ideas.txt");
        String s = FileSorter.sortFile(p,
                Pattern.compile("^[^#\\s}]+"),
                Pattern.compile("(?<=category = )\\w\\w\\w"),
                Pattern.compile("^[^#\\s}]+")
        );
        try(BufferedWriter w = Files.newBufferedWriter(p)) {
            w.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
