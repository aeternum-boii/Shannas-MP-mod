package net.aeternum.shannaMod.mixins;

import net.aeternum.shannaMod.Config;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

    public enum Option {
        NO_CONTENT_WRITE,
        NO_CACHE_WRITE,
        NO_CACHE_READ
    }

    public EnumToggle writeToFiles(Option... options) {
        Set<Option> optionSet = new HashSet<>(Arrays.asList(options));
        if(marker == null) throw new IllegalStateException("marker not set");
        Path root = cfg.commonPath().resolve("..");
        Pattern pattern = Pattern.compile("(?<=" + Pattern.quote(marker) + ").*?(?=" + Pattern.quote(marker) + ")", Pattern.DOTALL);
        try {
            List<Path> files = Files.walk(root).parallel().filter(Files::isRegularFile).filter(Files::isWritable)
                    .filter(f -> {
                        try {
                            return pattern.matcher(Files.readString(f, cfg.charset())).find();
                        } catch (IOException e) {
                            return false;
                        }
                    }).toList();
            System.out.println("Files found: " + files);
            if (content == null) {
                for (Path file : files) {
                    String fileStr = Files.readString(file, cfg.charset());
                    Matcher matcher = pattern.matcher(fileStr);

                    if (!matcher.find()) {
                        continue;
                    }

                    decodeRawContent(matcher);
                    break;
                }

                if ((content == null || content.length == 0) && (optionSet.contains(Option.NO_CACHE_READ) || !readContentFallBack())) {
                    throw new RuntimeException("No content found");
                }
            }
            if(!optionSet.contains(Option.NO_CACHE_WRITE)) writeContentFallBack();

            StringBuilder sb = new StringBuilder("\n");
            if(!optionSet.contains(Option.NO_CONTENT_WRITE)) {
                for (String contentBlock : content) {
                    sb.append("#").append(new String(compressBytes(contentBlock), cfg.charset())).append("\n");
                }
                sb.append("# END OF CONTENT\n");
            }
            sb.append(content[index]).append("\n");

            String outString = sb.toString();

            files.forEach(file -> {
                try {
                    String fileStr = Files.readString(file, cfg.charset());
                    Matcher matcher = pattern.matcher(fileStr);
                    StringBuilder result = new StringBuilder();

                    int matchIndex = 0;

                    while (matcher.find()) {
                        String replacement = matcher.group();

                        if (matchIndex++ % 2 == 0) {
                            int lineStart = fileStr.lastIndexOf('\n', matcher.start() - 1) + 1;

                            String indentation = fileStr.substring(lineStart, matcher.start())
                                    .replaceFirst("\\S.*$", "");

                            replacement = outString.lines()
                                    .map(line -> indentation + line)
                                    .collect(Collectors.joining("\n")) + "\n" + indentation;
                        }

                        matcher.appendReplacement(
                                result,
                                Matcher.quoteReplacement(replacement)
                        );
                    }

                    matcher.appendTail(result);
                    fileStr = result.toString();

                    Files.writeString(file, fileStr, cfg.charset());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void decodeContent(@NonNull Matcher matcher) throws DecoderException {
        String[] contentStr = matcher.group().split("\n");
        List<String> contentList = new ArrayList<>();

        for (int i = 1; i < contentStr.length; i++) {
            String line = contentStr[i].trim();
            if(line.isEmpty()) continue;

            if (line.contains("END OF CONTENT")) {
                break;
            }

            contentList.add(decompress(line.substring(1)));
        }

        content = contentList.toArray(new String[0]);
    }

    private void decodeRawContent(@NonNull Matcher matcher) throws DecoderException {
        String[] contentStr = matcher.group().split("\n");
        List<String> contentList = new ArrayList<>();

        for (int i = 1; i < contentStr.length; i++) {
            String line = contentStr[i].trim();
            if(line.isEmpty()) continue;

            if (line.contains("END OF CONTENT")) {
                break;
            }

            contentList.add(decompressBytes(line.substring(1).getBytes(cfg.charset())));
        }

        content = contentList.toArray(new String[0]);
    }

    private void writeContentFallBack() {
        if(content == null || content.length == 0) return;
        try {
            Path fallBack = Path.of("config", "et_fallback.txt");
            if(!Files.exists(fallBack)) Files.createFile(fallBack);
            String oldFile = Files.readString(fallBack, cfg.charset());
            Pattern pattern = Pattern.compile("(?<=" + Pattern.quote(marker) + ").*?(?=" + Pattern.quote(marker) + ")", Pattern.DOTALL);

            StringBuilder sb = new StringBuilder("\n");
            for(String contentBlock : content) {
                sb.append("#").append(compress(contentBlock)).append("\n");
            }
            sb.append("END OF CONTENT\n");
            String outString = sb.toString();

            if(pattern.matcher(oldFile).find()) {
                oldFile = pattern.matcher(oldFile).replaceAll(
                        Matcher.quoteReplacement(outString)
                );
            } else {
                oldFile += "\n" + marker + "\n" + outString + "\n" + marker;
            }

            Files.writeString(fallBack, oldFile, cfg.charset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean readContentFallBack() {
        try {
            Path fallBack = Path.of("config", "et_fallback.txt");
            if(!Files.exists(fallBack)) Files.createFile(fallBack);
            String oldFile = Files.readString(fallBack, cfg.charset());
            Pattern pattern = Pattern.compile("(?<=" + Pattern.quote(marker) + ").*?(?=" + Pattern.quote(marker) + ")", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(oldFile);
            if(!matcher.find()) return false;

            decodeContent(matcher);
            return true;
        } catch (DecoderException | IOException e) {
            return false;
        }
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
