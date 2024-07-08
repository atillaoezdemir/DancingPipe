package de.thws;

import lombok.Getter;

import javax.sound.midi.MidiMessage;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

@Getter
public class KeyboardPool {


    List<Keyboard> keyboards;
    private final long beatLengthInTicks;
    private Tempo tempoFactor;

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
            if (keyboards.get(i).getResolution() != firstResolution) {
                throw new OrganSequencerException("Error in Keyboard " + keyboards.get(i).getKeyboardName() + "!\nAll Keyboards should have the same resolution!");
            }
        }
        this.beatLengthInTicks = firstResolution * 4L;
        this.tempoFactor = Tempo.NORMAL;
    }

    public void setTempoFactor(Tempo tempoFactor) {
        this.tempoFactor = tempoFactor;
    }

    public void setTempoForPatterns(int index, boolean increase) {
        System.out.println(this.tempoFactor.name());
        keyboards.forEach(keyboard -> {
            for (int i = index; i < keyboard.getNumberOfPatterns(); i++) {
                Pattern currentPattern = keyboard.getKeyboardPatterns().get(i);
                for (int ii = 0; ii < currentPattern.getNumberOfMidiEvents(); ii++) {
                    OrganEvent oldEvent = currentPattern.getOrganEvent(ii);
                    MidiMessage oldMessage = oldEvent.getMessage();
                    int factoredTick = (int) (increase ? oldEvent.getTick() * 0.75f : oldEvent.getTick() * 1.25f);
                    currentPattern.setEvent(ii, new OrganEvent(oldMessage, factoredTick));
                }

            }
        });

            /*
            keyboard.getKeyboardPatterns().stream().forEach(pattern -> {
                for(int i = 0; i < pattern.getNumberOfMidiEvents(); i++) {
                    OrganEvent oldEvent = pattern.getOrganEvent(i);
                    MidiMessage oldMessage = oldEvent.getMessage();
                    int factoredTick = (int) (oldEvent.getTick() * this.tempoFactor);
                    pattern.setEvent(i, new OrganEvent(oldMessage, factoredTick));
                }
            });
        });

    }

             */
    }

    // todo this should be in OrganSequencer

    public void increaseTempo() {
        switch (this.tempoFactor) {
            case FASTER -> {
                this.tempoFactor = Tempo.FAST;
            }
            case NORMAL -> {
                this.tempoFactor = Tempo.FASTER;
            }
            case SLOWER -> {
                this.tempoFactor = Tempo.NORMAL;
            }
            case SLOW -> {
                this.tempoFactor = Tempo.SLOWER;
            }
        }
    }

    public void decreaseTempo() {
        switch (this.tempoFactor) {
            case FAST -> {
                this.tempoFactor = Tempo.FASTER;
            }
            case FASTER -> {
                this.tempoFactor = Tempo.NORMAL;
            }
            case NORMAL -> {
                this.tempoFactor = Tempo.SLOWER;
            }
            case SLOWER -> {
                this.tempoFactor = Tempo.SLOW;
            }
        }
    }

}
