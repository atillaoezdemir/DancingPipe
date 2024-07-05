package de.thws;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Pattern {
    private Sequence patternSequence;
    private Track patternTrack;
    private final int numberOfMidiEvents;

    Pattern(File path) throws OrganSequencerException {
        try {
            patternSequence = MidiSystem.getSequence(path);
        }
        catch (Exception e) {
            patternTrack = null;
            if(e.getClass().getName().equals("InvalidMidiDataException")) {
                throw new OrganSequencerException("MidiUnavailableException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the MIDI files are not damaged.");
            }
            else if (e.getClass().getName().equals("IOException")) {
                throw new OrganSequencerException("IOException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the folder with the MIDI files is not empty and the MIDI files are not damaged.");
            }
        }
        patternTrack = patternSequence.getTracks()[0];
        numberOfMidiEvents = patternTrack.size();
    }

    public Sequence getPatternSequence() {
        return patternSequence;
    }

    public Track getPatternTrack() {
        return patternTrack;
    }

    public MidiEvent getMidiEvent(int index) {
        return patternTrack.get(index);
    }

    public MidiEvent getLastMidiEvent() {
        return patternTrack.get(numberOfMidiEvents - 1);
    }

    public int getNumberOfMidiEvents() {
        return numberOfMidiEvents;
    }
}
