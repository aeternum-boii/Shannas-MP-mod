package net.aeternum.shannaMod.mixins;

import net.aeternum.shannaMod.Config;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * This class works in two ways:
 *  1. Minimal Markers are provided in the Source Material
 *   -> setMarker
 *   -> setContent
 *   -> toggleContent
 *   -> writeToFiles writes the identity to files and writes the toggled content
 *  2. Identity is in the files
 *   -> setContent can be skipped
 */
public class EnumToggle {
    private final Config cfg;
    private static final int BUF_SIZE = 2 << 6;

    private String marker = null;
    private String[] content = null;
    private int index = 0;

    public EnumToggle(Config cfg) {
        this.cfg = cfg;
    }
    @Contract(" -> new")
    public static @NonNull EnumToggle fromDefaultConfig() {
        return new EnumToggle(Config.readDefaultConfig());
    }

    /**
     * The full line.trim() must match this to start the mark. A marked section must start and end with {@code marker}
     * @param marker the marker
     * @return this object
     */
    public EnumToggle setMarker(String marker) {
        this.marker = marker;
        return this;
    }

    public EnumToggle setContent(String[] content) {
        this.content = content;
        return this;
    }

    public EnumToggle toggleContent(int contentIndex) {
        this.index = contentIndex;
        return this;
    }

    public EnumToggle writeToFiles() {
        if(marker == null) throw new IllegalStateException("marker not set");
        Path root = cfg.commonPath().resolve("..");
        try {
            List<Path> files = Files.walk(root).filter(Files::isRegularFile).filter(Files::isWritable).filter(f -> f.endsWith(".txt"))
                    .filter(f -> {
                        try {
                            String[] lines = Files.readString(f).split("\n");
                            for(String line : lines) {
                                line = line.trim();
                                if(line.equals(marker)) return true;
                            }
                        } catch (IOException e) {
                            return false;
                        }
                        return false;
                    }).toList();
            Pattern pattern = Pattern.compile("(?<=" + Pattern.quote(marker) + ").*?(?=" + Pattern.quote(marker) + ")", Pattern.DOTALL);
            if (content == null) {
                for (Path file : files) {
                    String fileStr = Files.readString(file);
                    Matcher matcher = pattern.matcher(fileStr);

                    if (!matcher.find()) {
                        continue;
                    }

                    String[] contentStr = matcher.group().split("\n");
                    List<String> contentList = new ArrayList<>();

                    for (int i = 1; i < contentStr.length; i++) {
                        String line = contentStr[i].trim();

                        if (line.contains("END OF CONTENT")) {
                            break;
                        }

                        contentList.add(decompress(line.substring(1)));
                    }

                    content = contentList.toArray(new String[0]);
                    break;
                }

                if (content == null) {
                    return this;
                }
            }

            StringBuilder sb = new StringBuilder();
            for(String contentBlock : content) {
                sb.append("#").append(compress(contentBlock)).append("\n");
            }
            sb.append("# END OF CONTENT\n");
            sb.append(content[index]);

            String outString = sb.toString();

            files.forEach(file -> {
                try {
                    String fileStr = Files.readString(file);
                    fileStr = pattern.matcher(fileStr).replaceAll(
                            Matcher.quoteReplacement(outString)
                    );
                    Files.writeString(file, fileStr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    static void main() {
        EnumToggle enumToggle = new EnumToggle(Config.readDefaultConfig())
                .setMarker("# ET-Marker: 001 #")
                .setContent(new String[]{"# Test 1", "# Test 2"})
                .toggleContent(0)
                .writeToFiles();
    }





    // This all needs refactoring into a seperate class
    private static byte @NonNull [] compressBytes(@NonNull String s) {
        byte[] input = s.getBytes(StandardCharsets.UTF_8);

        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        byte[] buffer = new byte[BUF_SIZE];
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        while (!deflater.finished()) {
            int length = deflater.deflate(buffer);
            output.write(buffer, 0, length);
        }

        deflater.end();
        return output.toByteArray();
    }

    private static @NonNull String compress(@NonNull String s) {
        return formatHexStr(Hex.encodeHexString(compressBytes(s)));
    }

    private static @NonNull String decompress(@NonNull String s) throws DecoderException {
        return decompressBytes(Hex.decodeHex(reverseFormatHexStr(s)));
    }

    private static @NonNull String decompressBytes(byte @NonNull [] input) {
        Inflater inflater = new Inflater();
        inflater.setInput(input);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[BUF_SIZE];

            while (!inflater.finished()) {
                int length = inflater.inflate(buffer);

                if (length == 0 && inflater.needsInput()) {
                    throw new IllegalArgumentException("Invalid compressed data");
                }

                output.write(buffer, 0, length);
            }
        } catch (DataFormatException e) {
            throw new IllegalArgumentException("Invalid compressed data", e);
        } finally {
            inflater.end();
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    private static @NonNull String formatHexStr(@NonNull String hexStr) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hexStr.length(); i++) {
            if (i % 2 == 0 && i != 0) {
                sb.append(' ');
            }

            sb.append(Character.toUpperCase(hexStr.charAt(i)));
        }

        return sb.toString();
    }

    private static @NonNull String reverseFormatHexStr(@NonNull String hexStr) {
        return hexStr.replace(" ", "").toLowerCase(Locale.ROOT);
    }
}
