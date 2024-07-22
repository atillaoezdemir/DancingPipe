package de.thws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.*;

@Getter
public class Configurator implements Serializable {
    String pieceName;
    String pieceComposer;
    String pieceFolderPath;
    int numberOfPatternsPerKeyboard;
    int lengthOfPatternInBars;
    int lengthOfPieceInBars;
    int numberOfKeyboards;
    float tempoFactor;

    public Configurator() throws IOException, ClassNotFoundException {
    };

    public Configurator(Configurator configurator) {
        this.pieceName = configurator.pieceName;
        this.pieceComposer = configurator.pieceComposer;
        this.pieceFolderPath = configurator.pieceFolderPath;
        this.numberOfPatternsPerKeyboard = configurator.numberOfPatternsPerKeyboard;
        this.lengthOfPatternInBars = configurator.lengthOfPatternInBars;
        this.lengthOfPieceInBars = configurator.lengthOfPieceInBars;
        this.numberOfKeyboards = configurator.numberOfKeyboards;
        this.tempoFactor = configurator.tempoFactor;
    }
    public static Configurator loadFromFile(String filePath) throws IOException {
        File file = new File(filePath);

        FileInputStream fis = new FileInputStream(file);
        String json = new String(fis.readAllBytes());

        ObjectMapper mapper = new ObjectMapper();

        Configurator obj = mapper.readValue(json, Configurator.class);
        fis.close();

        return obj;

    }

    public void saveToFile() throws IOException, ClassNotFoundException {
        Configurator config = new Configurator();
        config.pieceName = "test";

        FileOutputStream file = new FileOutputStream
                ("server-config.json");
        ObjectOutputStream out = new ObjectOutputStream
                (file);

        // Method for serialization of object
        out.writeObject(config);

        out.close();
        file.close();
    }
}
