
package de.thws.mapper;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMapper {
    public double lowerBorder;
    public double upperBorder;
    public String filepath;

    public FileMapper(String filepath, double lowerBorder, double upperBorder) {
        this.filepath = filepath;
        this.lowerBorder = lowerBorder;
        this.upperBorder = upperBorder;
    }

    public static FileMapper[] loadMappers(String jsonFilePath) {
        File jsonFile = new File(jsonFilePath);
        String jsonFileContent;
        try {
            jsonFileContent = Files.readString(jsonFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + jsonFilePath, e);
        }

        return new Gson().fromJson(jsonFileContent, FileMapper[].class);
    }
}