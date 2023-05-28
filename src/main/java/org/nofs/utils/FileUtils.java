package org.nofs.utils;

import dev.morphia.mapping.Mapper;
import org.nofs.Grasscutter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/* loaded from: sdkserver.jar:emu/grasscutter/utils/FileUtils.class */
public final class FileUtils {
    public static void write(String dest, byte[] bytes) {
        Path path = Path.of(dest, new String[0]);
        try {
            Files.write(path, bytes, new OpenOption[0]);
        } catch (IOException e) {
            Grasscutter.getLogger().warn("Failed to write file: " + dest);
        }
    }

    public static byte[] read(String dest) {
        return read(Path.of(dest, new String[0]));
    }

    public static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            Grasscutter.getLogger().warn("Failed to read file: " + path);
            return new byte[0];
        }
    }

    public static InputStream readResourceAsStream(String resourcePath) {
        return Grasscutter.class.getResourceAsStream(resourcePath);
    }

    public static byte[] readResource(String resourcePath) {
        try {
            InputStream is = Grasscutter.class.getResourceAsStream(resourcePath);
            byte[] readAllBytes = is.readAllBytes();
            if (is != null) {
                is.close();
            }
            return readAllBytes;
        } catch (Exception exception) {
            Grasscutter.getLogger().warn("Failed to read resource: " + resourcePath);
            exception.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] read(File file) {
        return read(file.getPath());
    }

    public static void copyResource(String resourcePath, String destination) {
        try {
            byte[] resource = readResource(resourcePath);
            write(destination, resource);
        } catch (Exception exception) {
            Grasscutter.getLogger().warn("Failed to copy resource: " + resourcePath + "\n" + exception);
        }
    }

    public static String getFilenameWithoutPath(String fileName) {
        if (fileName.indexOf(Mapper.IGNORED_FIELDNAME) > 0) {
            return fileName.substring(0, fileName.lastIndexOf(Mapper.IGNORED_FIELDNAME));
        }
        return fileName;
    }

    public static List<Path> getPathsFromResource(String folder) throws URISyntaxException {
        try {
            return (List) Files.walk(Path.of(Grasscutter.class.getResource(folder).toURI()), new FileVisitOption[0]).filter(x$0 -> {
                return Files.isRegularFile(x$0, new LinkOption[0]);
            }).collect(Collectors.toList());
        } catch (IOException | FileSystemNotFoundException e) {
            try {
                return (List) Files.walk(Path.of(System.getProperty("user.dir"), new String[]{folder}), new FileVisitOption[0]).filter(x$02 -> {
                    return Files.isRegularFile(x$02, new LinkOption[0]);
                }).collect(Collectors.toList());
            } catch (IOException e2) {
                return null;
            }
        }
    }

    public static String readToString(InputStream file) throws IOException {
        byte[] content = file.readAllBytes();
        return new String(content, StandardCharsets.UTF_8);
    }
}
