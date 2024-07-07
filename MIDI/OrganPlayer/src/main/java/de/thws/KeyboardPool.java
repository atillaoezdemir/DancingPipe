package de.thws;

import lombok.Getter;

import javax.sound.midi.MidiMessage;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

@Getter
public class KeyboardPool {

    public static final float FASTER = 0.75F;
    public static final float VERY_FAST = 0.5F;
    public static final float SLOWER = 1.25F;
    public static final float VERY_SLOW = 1.5F;


    List<Keyboard> keyboards;
    private final long beatLengthInTicks;
    private float tempoFactor;

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
        this.tempoFactor = 1.0F;
    }

    public void setTempo(float factor) {
        this.tempoFactor = factor;
        keyboards.forEach(keyboard -> {
            keyboard.getKeyboardPatterns().stream().forEach(pattern -> {
                for(int i = 0; i < pattern.getNumberOfMidiEvents(); i++) {
                    OrganEvent oldEvent = pattern.getOrganEvent(i);
                    MidiMessage oldMessage = oldEvent.getMessage();
                    int factoredTick = (int) (oldEvent.getTick() * factor);
                    pattern.setEvent(i, new OrganEvent(oldMessage, factoredTick));
                }
            });
        });

    }

}
