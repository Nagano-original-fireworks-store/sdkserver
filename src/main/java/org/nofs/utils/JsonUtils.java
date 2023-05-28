package org.nofs.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/* loaded from: sdkserver.jar:emu/grasscutter/utils/JsonUtils.class */
public final class JsonUtils {
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Gson getGsonFactory() {
        return gson;
    }

    public static String encode(Object object) {
        return gson.toJson(object);
    }

    public static <T> T decode(JsonElement jsonElement, Class<T> classType) throws JsonSyntaxException {
        return (T) gson.fromJson(jsonElement, (Class<Object>) classType);
    }

    public static <T> T loadToClass(Reader fileReader, Class<T> classType) throws IOException {
        return (T) gson.fromJson(fileReader, (Class<Object>) classType);
    }

    @Deprecated
    public static <T> T loadToClass(String filename, Class<T> classType) throws IOException {
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(Utils.toFilePath(filename)), StandardCharsets.UTF_8);
        try {
            T t = (T) loadToClass(fileReader, classType);
            fileReader.close();
            return t;
        } catch (Throwable th) {
            try {
                fileReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T> T loadToClass(Path filename, Class<T> classType) throws IOException {
        BufferedReader fileReader = Files.newBufferedReader(filename, StandardCharsets.UTF_8);
        try {
            T t = (T) loadToClass(fileReader, classType);
            if (fileReader != null) {
                fileReader.close();
            }
            return t;
        } catch (Throwable th) {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static <T> List<T> loadToList(Reader fileReader, Class<T> classType) throws IOException {
        return (List) gson.fromJson(fileReader, TypeToken.getParameterized(List.class, classType).getType());
    }

    @Deprecated
    public static <T> List<T> loadToList(String filename, Class<T> classType) throws IOException {
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(Utils.toFilePath(filename)), StandardCharsets.UTF_8);
        try {
            List<T> loadToList = loadToList(fileReader, classType);
            fileReader.close();
            return loadToList;
        } catch (Throwable th) {
            try {
                fileReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T> List<T> loadToList(Path filename, Class<T> classType) throws IOException {
        BufferedReader fileReader = Files.newBufferedReader(filename, StandardCharsets.UTF_8);
        try {
            List<T> loadToList = loadToList(fileReader, classType);
            if (fileReader != null) {
                fileReader.close();
            }
            return loadToList;
        } catch (Throwable th) {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static <T1, T2> Map<T1, T2> loadToMap(Reader fileReader, Class<T1> keyType, Class<T2> valueType) throws IOException {
        return (Map) gson.fromJson(fileReader, TypeToken.getParameterized(Map.class, keyType, valueType).getType());
    }

    @Deprecated
    public static <T1, T2> Map<T1, T2> loadToMap(String filename, Class<T1> keyType, Class<T2> valueType) throws IOException {
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(Utils.toFilePath(filename)), StandardCharsets.UTF_8);
        try {
            Map<T1, T2> loadToMap = loadToMap(fileReader, keyType, valueType);
            fileReader.close();
            return loadToMap;
        } catch (Throwable th) {
            try {
                fileReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static <T1, T2> Map<T1, T2> loadToMap(Path filename, Class<T1> keyType, Class<T2> valueType) throws IOException {
        BufferedReader fileReader = Files.newBufferedReader(filename, StandardCharsets.UTF_8);
        try {
            Map<T1, T2> loadToMap = loadToMap(fileReader, keyType, valueType);
            if (fileReader != null) {
                fileReader.close();
            }
            return loadToMap;
        } catch (Throwable th) {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static <T> T decode(String jsonData, Class<T> classType) {
        try {
            return (T) gson.fromJson(jsonData, (Class<Object>) classType);
        } catch (Exception e) {
            return null;
        }
    }
}
