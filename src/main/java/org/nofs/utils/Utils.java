package org.nofs.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.nofs.config.ConfigContainer;
import org.nofs.data.DataLoader;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/Utils.class */
public final class Utils {
    public static final Random random = new Random();
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static int randomRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static float randomFloatRange(float min, float max) {
        return (random.nextFloat() * (max - min)) + min;
    }

    public static int getCurrentSeconds() {
        return (int) (System.currentTimeMillis() / 1000.0d);
    }

    public static String lowerCaseFirstChar(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String toString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int read = bis.read();
        while (true) {
            int result = read;
            if (result != -1) {
                buf.write((byte) result);
                read = bis.read();
            } else {
                return buf.toString();
            }
        }
    }

    public static void logByteArray(byte[] array) {
        ByteBuf b = Unpooled.wrappedBuffer(array);
        org.nofs.sdkserver.getLogger().info("\n" + ByteBufUtil.prettyHexDump(b));
        b.release();
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[(j * 2) + 1] = HEX_ARRAY[v & 15];
        }
        return new String(hexChars);
    }

    public static String bytesToHex(ByteBuf buf) {
        return bytesToHex(byteBufToArray(buf));
    }

    public static byte[] byteBufToArray(ByteBuf buf) {
        byte[] bytes = new byte[buf.capacity()];
        buf.getBytes(0, bytes);
        return bytes;
    }

    public static int abilityHash(String str) {
        int v7 = 0;
        int v8 = 0;
        while (v8 < str.length()) {
            int i = v8;
            v8++;
            v7 = str.charAt(i) + (131 * v7);
        }
        return v7;
    }

    public static String toFilePath(String path) {
        return path.replace("/", File.separator);
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static boolean createFolder(String path) {
        return new File(path).mkdirs();
    }

    public static boolean copyFromResources(String resource, String destination) {
        try {
            InputStream stream = org.nofs.sdkserver.class.getResourceAsStream(resource);
            if (stream == null) {
                org.nofs.sdkserver.getLogger().warn("Could not find resource: " + resource);
                if (stream != null) {
                    stream.close();
                }
                return false;
            }
            Files.copy(stream, new File(destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (stream != null) {
                stream.close();
            }
            return true;
        } catch (Exception exception) {
            org.nofs.sdkserver.getLogger().warn("Unable to copy resource " + resource + " to " + destination, (Throwable) exception);
            return false;
        }
    }

    public static void logObject(Object object) {
        org.nofs.sdkserver.getLogger().info(JsonUtils.encode(object));
    }

    public static void startupCheck() {
        ConfigContainer config = org.nofs.sdkserver.getConfig();
        String dataFolder = config.folderStructure.data;
        if (!fileExists(dataFolder)) {
            createFolder(dataFolder);
        }
        DataLoader.checkAllFiles();
        if (0 != 0) {
            System.exit(1);
        }
    }

    public static int getNextTimestampOfThisHour(int hour, String timeZone, int param) {
        ZonedDateTime withSecond;
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        for (int i = 0; i < param; i++) {
            if (zonedDateTime.getHour() < hour) {
                withSecond = zonedDateTime.withHour(hour).withMinute(0).withSecond(0);
            } else {
                withSecond = zonedDateTime.plusDays(1L).withHour(hour).withMinute(0).withSecond(0);
            }
            zonedDateTime = withSecond;
        }
        return (int) zonedDateTime.toInstant().atZone(ZoneOffset.UTC).toEpochSecond();
    }

    public static int getNextTimestampOfThisHourInNextWeek(int hour, String timeZone, int param) {
        ZonedDateTime withSecond;
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        for (int i = 0; i < param; i++) {
            if (zonedDateTime.getDayOfWeek() == DayOfWeek.MONDAY && zonedDateTime.getHour() < hour) {
                withSecond = ZonedDateTime.now(ZoneId.of(timeZone)).withHour(hour).withMinute(0).withSecond(0);
            } else {
                withSecond = zonedDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(hour).withMinute(0).withSecond(0);
            }
            zonedDateTime = withSecond;
        }
        return (int) zonedDateTime.toInstant().atZone(ZoneOffset.UTC).toEpochSecond();
    }

    public static int getNextTimestampOfThisHourInNextMonth(int hour, String timeZone, int param) {
        ZonedDateTime withSecond;
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone));
        for (int i = 0; i < param; i++) {
            if (zonedDateTime.getDayOfMonth() == 1 && zonedDateTime.getHour() < hour) {
                withSecond = ZonedDateTime.now(ZoneId.of(timeZone)).withHour(hour).withMinute(0).withSecond(0);
            } else {
                withSecond = zonedDateTime.with(TemporalAdjusters.firstDayOfNextMonth()).withHour(hour).withMinute(0).withSecond(0);
            }
            zonedDateTime = withSecond;
        }
        return (int) zonedDateTime.toInstant().atZone(ZoneOffset.UTC).toEpochSecond();
    }

    public static String readFromInputStream(@Nullable InputStream stream) {
        if (stream == null) {
            return "empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    stringBuilder.append(line);
                } catch (Throwable th) {
                    try {
                        reader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            stream.close();
            reader.close();
        } catch (IOException e) {
            org.nofs.sdkserver.getLogger().warn("Failed to read from input stream.");
        } catch (NullPointerException e2) {
            return "empty";
        }
        return stringBuilder.toString();
    }

    public static int lerp(int x, int[][] xyArray) {
        try {
            if (x <= xyArray[0][0]) {
                return xyArray[0][1];
            }
            if (x >= xyArray[xyArray.length - 1][0]) {
                return xyArray[xyArray.length - 1][1];
            }
            for (int i = 0; i < xyArray.length - 1; i++) {
                if (x == xyArray[i + 1][0]) {
                    return xyArray[i + 1][1];
                }
                if (x < xyArray[i + 1][0]) {
                    int position = x - xyArray[i][0];
                    int fullDist = xyArray[i + 1][0] - xyArray[i][0];
                    int prevValue = xyArray[i][1];
                    int fullDelta = xyArray[i + 1][1] - prevValue;
                    return prevValue + ((position * fullDelta) / fullDist);
                }
            }
            return 0;
        } catch (IndexOutOfBoundsException e) {
            org.nofs.sdkserver.getLogger().error("Malformed lerp point array. Must be of form [[x0, y0], ..., [xN, yN]].");
            return 0;
        }
    }

    public static boolean intInArray(int key, int[] array) {
        for (int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }

    public static int[] setSubtract(int[] minuend, int[] subtrahend) {
        IntList temp = new IntArrayList();
        for (int i : minuend) {
            if (!intInArray(i, subtrahend)) {
                temp.add(i);
            }
        }
        return temp.toIntArray();
    }

    public static String getLanguageCode(Locale locale) {
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    public static String base64Encode(byte[] toEncode) {
        return Base64.getEncoder().encodeToString(toEncode);
    }

    public static byte[] base64Decode(String toDecode) {
        return Base64.getDecoder().decode(toDecode);
    }

    public static <T> T drawRandomListElement(List<T> list, List<Integer> probabilities) {
        if (probabilities == null || probabilities.size() <= 1 || probabilities.size() != list.size()) {
            int index = ThreadLocalRandom.current().nextInt(0, list.size());
            return list.get(index);
        }
        int totalProbabilityMass = probabilities.stream().reduce((v0, v1) -> {
            return Integer.sum(v0, v1);
        }).get().intValue();
        int roll = ThreadLocalRandom.current().nextInt(1, totalProbabilityMass + 1);
        int currentTotalChance = 0;
        for (int i = 0; i < list.size(); i++) {
            currentTotalChance += probabilities.get(i).intValue();
            if (roll <= currentTotalChance) {
                return list.get(i);
            }
        }
        return list.get(0);
    }

    public static <T> T drawRandomListElement(List<T> list) {
        return (T) drawRandomListElement(list, null);
    }
}
