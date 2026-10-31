package net.aeternum.shannaMod.parsing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSorter {
    static void main() {
        Path p = Path.of("..", "mod", "common", "ideas", "00_basic_ideas.txt");
        String s = sortFile(p,
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


    public static String sortFile(Path file, Pattern blockIdentifier, Pattern firstLevelComparator, Pattern secondLevelComparator) {
        String[] fileLines;
        try {
            fileLines = Files.readAllLines(file).toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<Integer> blockIndexes = new HashSet<>();
        for (int i = 0; i < fileLines.length; i++) {
            Matcher m = blockIdentifier.matcher(fileLines[i]);
            if (m.find()) {
                blockIndexes.add(i);
            }
        }

        List<String> blocks = new ArrayList<>();
        String preBlock = null;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fileLines.length; i++) {
            if(blockIndexes.contains(i)) {
                if(preBlock == null) preBlock = sb.toString();
                else blocks.add(sb.toString());
                sb = new StringBuilder();
            }
            sb.append(fileLines[i]).append("\n");
        }

        blocks.add(sb.toString());
        blocks.sort(
                (block1, block2) -> {
                    Matcher block1Matcher = firstLevelComparator.matcher(block1);
                    Matcher block2Matcher = firstLevelComparator.matcher(block2);
                    if(!block1Matcher.find() & !block2Matcher.find()) return 0;
                    if(!block1Matcher.hasMatch()) return -1;
                    if(!block2Matcher.hasMatch()) return 1;

                    if(block1Matcher.group().compareTo(block2Matcher.group()) != 0) return block1Matcher.group().compareTo(block2Matcher.group());

                    block1Matcher = secondLevelComparator.matcher(block1);
                    block2Matcher = secondLevelComparator.matcher(block2);

                    if(!block1Matcher.find() & !block2Matcher.find()) return 0;
                    if(!block1Matcher.hasMatch()) return -1;
                    if(!block2Matcher.hasMatch()) return 1;

                    return block1Matcher.group().compareTo(block2Matcher.group());
                }
        );

        sb = new StringBuilder();
        sb.append(preBlock).append("\n");
        for (String block : blocks) sb.append(block).append("\n");

        return sb.toString();
    }
}
