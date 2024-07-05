package de.thws;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Keyboard {
    private final List<Pattern> keyboardPatterns;
    private final int numberOfPatterns;
    private final long lastTick;
    private boolean active;

    Keyboard(File keyboardDirectory) {
        active = false;
        if (!keyboardDirectory.isDirectory() || keyboardDirectory.listFiles() == null) {
            keyboardPatterns = null;
            numberOfPatterns = 0;
            lastTick = 0;
            return;
        }
        List<File> files = Arrays.asList(keyboardDirectory.listFiles());
        numberOfPatterns = files.size();

        keyboardPatterns = files
                .stream()
                .map(f -> {
                    try {
                        return new Pattern(f.getAbsoluteFile());
                    } catch (OrganSequencerException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());


        long temp;
        temp = 0;
        for(Pattern pattern : keyboardPatterns) {
            int numberOfMidiEvents = pattern.getNumberOfMidiEvents();
            temp += pattern.getMidiEvent(numberOfMidiEvents - 1).getTick();
        }
        lastTick = temp;
    }

    public List<Sequence> getSequences() {
        return keyboardPatterns
                .stream()
                .map(Pattern::getPatternSequence)
                .collect(Collectors.toList());
    }

    public Track getFirstTrack() {
        return getSequences().getFirst().getTracks()[0];
    }

    public int getFirstTrackNumberOfEvents() {
        return getFirstTrack().size();
    }

    public List<Track> getTracks() {
        return getSequences()
                .stream()
                .map(s -> s.getTracks()[0])
                .collect(Collectors.toList());
    }


    public List<Pattern> getKeyboardPatterns() {
        return keyboardPatterns;
    }

    public int getNumberOfPatterns() {
        return numberOfPatterns;
    }

    public long getLastTick() {
        return lastTick;
    }

    public boolean isActive() {
        return active;
    }

    public void makeActive() {
        active = true;
    }

    public void makeInactive()  {
        active = false;
    }
}
