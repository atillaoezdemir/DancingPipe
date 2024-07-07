package de.thws;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

@Getter
public class KeyboardPool {

    List<Keyboard> keyboards;
    private final long beatLengthInTicks;

    public KeyboardPool(File directoryPath) throws OrganSequencerException {

        //todo add check if directory null

        keyboards = Arrays.stream(directoryPath.listFiles())
                                .map(file -> {
                                    if (file.isDirectory()) {
                                        return new Keyboard(file);
                                    }
                                    throw new RuntimeException("Not a directory: " + file);
                                }).collect(Collectors.toList());

        int firstResolution = keyboards.getFirst().getResolution();
        for (int i = 0; i < keyboards.size(); i++) {
            if(keyboards.get(i).getResolution() != firstResolution) {
                throw new OrganSequencerException("Error in Keyboard " + keyboards.get(i).getKeyboardName() + "!\nAll Keyboards should have the same resolution!");
            }
        }
        this.beatLengthInTicks = firstResolution * 4L;
    }

}
