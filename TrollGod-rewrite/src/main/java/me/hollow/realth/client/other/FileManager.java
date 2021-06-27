package me.hollow.realth.client.other;

import me.hollow.realth.client.module.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    private final Path base;

    private static Path lookupPath(Path root, String... paths) {
        return Paths.get(root.toString(), paths);
    }

    private Path getRoot() {
        return Paths.get("");
    }

    private static void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir)) {
                if (Files.exists(dir)) {
                    Files.delete(dir); // delete if it exists but isn't a directory
                }

                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getMkDirectory(Path parent, String... paths) {
        if (paths.length < 1) {
            return parent;
        }
        Path dir = lookupPath(parent, paths);
        createDirectory(dir);
        return dir;
    }

    public FileManager() {
        base = getMkDirectory(getRoot(), "Realth/modules/");
        for (Module.Category category : Module.Category.values()) {
            getMkDirectory(base, category.name());
        }
    }

}
