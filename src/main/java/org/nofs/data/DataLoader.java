package org.nofs.data;

import org.nofs.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DataLoader {

    public static InputStream load(String resourcePath) throws FileNotFoundException {
        return load(resourcePath, true);
    }



    public static InputStream load(String resourcePath, boolean useFallback) throws FileNotFoundException {
        Path path = useFallback
                ? FileUtils.getDataPath(resourcePath)
                : FileUtils.getDataUserPath(resourcePath);
        if (Files.exists(path)) {
            // Data is in the resource directory
            try {
                return Files.newInputStream(path);
            } catch (IOException e) {
                throw new FileNotFoundException(e.getMessage());  // This is evil but so is changing the function signature at this point
            }
        }
        return null;
    }



    public static void checkAllFiles() {
        try {
            List<Path> filenames = FileUtils.getPathsFromResource("/data/");

            if (filenames == null) {
                org.nofs.sdkserver.getLogger().error("We were unable to locate your default data files.");
            } else for (Path file : filenames) {
                 String relativePath = String.valueOf(file).split("[\\\\\\/]data[\\\\\\/]")[1];

                 checkAndCopyData(relativePath);
            }
        } catch (Exception e) {
            org.nofs.sdkserver.getLogger().error("An error occurred while trying to check the data folder.", e);
        }
    }

    private static void checkAndCopyData(String name) {
        // TODO: Revisit this if default dumping is ever reintroduced
        Path filePath = FileUtils.getDataPath(name);

        if (!Files.exists(filePath)) {
            var root = filePath.getParent();
            if (root.toFile().mkdirs())
                org.nofs.sdkserver.getLogger().info("Created data folder '" + root + "'");

            org.nofs.sdkserver.getLogger().info("Creating default '" + name + "' data");
            FileUtils.copyResource("/data/" + name, filePath.toString());
        }
    }
}
