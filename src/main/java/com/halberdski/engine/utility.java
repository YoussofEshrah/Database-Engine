package com.halberdski.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class utility {
    private static String pathToMetadataFile = "./src/main/resources/metadata.csv";
    private static String pathToTablesFolder = "./src/main/resources/Serialized_Tables/";
    private static String pathToPagesFolder = "./src/main/resources/Serialized_Pages/";
    private static String pathToIndicesFolder = "./src/main/resources/Serialized_Indices/";

    // private static String pathToConfig = System.getProperty("user.dir") +
    // "\\src\\main\\resources\\DBApp.config";

    public static void clearDatabaseSystem() {
        Path tablesFolder = Paths.get(pathToTablesFolder);
        deleteAndRecreateFolder(tablesFolder);
        Path pagesFolder = Paths.get(pathToPagesFolder);
        deleteAndRecreateFolder(pagesFolder);
        Path indicesFolder = Paths.get(pathToIndicesFolder);
        deleteAndRecreateFolder(indicesFolder);
        recreateMetaDataFile();
        System.out.println("Database Cleared");
    }

    public static void deleteAndRecreateFolder(Path folderPath) {
        try {
            if (Files.exists(folderPath)) {
                Files.walk(folderPath)
                        .sorted((a, b) -> -a.compareTo(b))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException ignored) {
                            }
                        });
            }
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void recreateMetaDataFile() {
        File file = new File(pathToMetadataFile);

        if (file.exists()) {
            if (file.delete()) {
            } else {
                return; // Exit if failed to delete
            }
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        clearDatabaseSystem();
    }
}
