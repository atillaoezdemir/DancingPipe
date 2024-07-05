package de.thws;

import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

public class KeyboardPool {
    List<Keyboard> keyboards;

    public KeyboardPool(File directoryPath) {

        //todo add check if directory null

        keyboards = Arrays.stream(directoryPath.listFiles())
                                .map(file -> {
                                    if (file.isDirectory()) {
                                        return new Keyboard(file);
                                    }
                                    throw new RuntimeException("Not a directory: " + file);
                                }).collect(Collectors.toList());
    }

    public List<Keyboard> getKeyboards() {
        return keyboards;
    }
}
