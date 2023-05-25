package org.nofs.data;

import org.nofs.config.Configuration;
import org.nofs.utils.FileUtils;
import org.nofs.utils.JsonUtils;
import org.nofs.utils.Utils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/* loaded from: org.nofs.jar:emu/org.nofs/data/DataLoader.class */
public class DataLoader {
    public static InputStream load(String resourcePath) throws FileNotFoundException {
        return load(resourcePath, true);
    }

    public static InputStreamReader loadReader(String resourcePath) throws IOException, FileNotFoundException {
        try {
            InputStream is = load(resourcePath, true);
            return new InputStreamReader(is);
        } catch (FileNotFoundException exception) {
            throw exception;
        }
    }

    public static InputStream load(String resourcePath, boolean useFallback) throws FileNotFoundException {
        if (Utils.fileExists(Configuration.DATA(resourcePath))) {
            return new FileInputStream(Configuration.DATA(resourcePath));
        }
        if (useFallback) {
            return FileUtils.readResourceAsStream("/defaults/data/" + resourcePath);
        }
        return null;
    }

    public static <T> T loadClass(String resourcePath, Class<T> classType) throws IOException {
        InputStreamReader reader = loadReader(resourcePath);
        try {
            T t = (T) JsonUtils.loadToClass(reader, classType);
            if (reader != null) {
                reader.close();
            }
            return t;
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static <T> List<T> loadList(String resourcePath, Class<T> classType) throws IOException {
        InputStreamReader reader = loadReader(resourcePath);
        try {
            List<T> loadToList = JsonUtils.loadToList(reader, classType);
            if (reader != null) {
                reader.close();
            }
            return loadToList;
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static <T1, T2> Map<T1, T2> loadMap(String resourcePath, Class<T1> keyType, Class<T2> valueType) throws IOException {
        InputStreamReader reader = loadReader(resourcePath);
        try {
            Map<T1, T2> loadToMap = JsonUtils.loadToMap(reader, keyType, valueType);
            if (reader != null) {
                reader.close();
            }
            return loadToMap;
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static void checkAllFiles() {
        try {
            List<Path> filenames = FileUtils.getPathsFromResource("/defaults/data/");
            if (filenames == null) {
                org.nofs.sdkserver.getLogger().error("We were unable to locate your default data files.");
            } else {
                for (Path file : filenames) {
                    String relativePath = String.valueOf(file).split("defaults[\\\\\\/]data[\\\\\\/]")[1];
                    checkAndCopyData(relativePath);
                }
            }
        } catch (Exception e) {
            org.nofs.sdkserver.getLogger().error("An error occurred while trying to check the data folder.", (Throwable) e);
        }
    }

    private static void checkAndCopyData(String name) {
        String filePath = Utils.toFilePath(Configuration.DATA(name));
        if (!Utils.fileExists(filePath)) {
            if (name.contains("/")) {
                String[] path = name.split("/");
                String folder = "";
                for (int i = 0; i < path.length - 1; i++) {
                    folder = folder + path[i] + "/";
                    String folderToCreate = Utils.toFilePath(Configuration.DATA(folder));
                    if (!Utils.fileExists(folderToCreate)) {
                        org.nofs.sdkserver.getLogger().info("Creating data folder '" + folder + "'");
                        Utils.createFolder(folderToCreate);
                    }
                }
            }
            org.nofs.sdkserver.getLogger().info("Creating default '" + name + "' data");
            FileUtils.copyResource("/defaults/data/" + name, filePath);
        }
    }
}
