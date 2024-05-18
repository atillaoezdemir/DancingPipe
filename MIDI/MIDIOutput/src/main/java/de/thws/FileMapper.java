package de.thws;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileMapper {
    double lowerBorder;
    double upperBorder;
    String filepath;

    public FileMapper(String filepath, double lowerBorder, double upperBorder) {
        this.filepath = filepath;
        this.lowerBorder = lowerBorder;
        this.upperBorder = upperBorder;
    }

    public FileMapper(File jsonFile) {
        String jsonFileContent = null;
        try {
            jsonFileContent = Files.readString(jsonFile.toPath());
        }
        catch (IOException e) {
            System.err.println("Error in reading JSON file: " + jsonFile.getAbsolutePath());
        }
        if (jsonFileContent != null) {
            Gson gson = new Gson();
            FileMapper fm = gson.fromJson(jsonFileContent, FileMapper.class);
            this.filepath = fm.filepath;
            this.lowerBorder = fm.lowerBorder;
            this.upperBorder = fm.upperBorder;
        }
    }
    public String toJson() {
        return new Gson().toJson(this);

    }

}