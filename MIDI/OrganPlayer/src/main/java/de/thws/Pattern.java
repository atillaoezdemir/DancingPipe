package de.thws;

import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.File;

@Setter
@Getter
public class Pattern {
    private int patternIndex;
    private boolean canBeInterrupted;
    //private Sequence patternSequence;
    private OrganSequence organSequence;
    //private Track patternTrack;
    private int numberOfMidiEvents;
    private final String patternName;
    private boolean isEmpty;

    public Pattern() {
        this.isEmpty = true;
        this.organSequence = null;
        this.numberOfMidiEvents = 0;
        this.patternName = "";

    }

    public Pattern(File path) throws OrganSequencerException {
        this.patternName = path.getName();
        try {
            this.organSequence = new OrganSequence(MidiSystem.getSequence(path));
        }
        catch (Exception e) {
            //patternTrack = null;
            if(e.getClass().getName().equals("InvalidMidiDataException")) {
                throw new OrganSequencerException("MidiUnavailableException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the MIDI files are not damaged.");
            }
            else if (e.getClass().getName().equals("IOException")) {
                throw new OrganSequencerException("IOException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the folder with the MIDI files is not empty and the MIDI files are not damaged.");
            }
            else {
                throw new OrganSequencerException("NullPointerException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the right MIDI file is referenced or exists.");
            }
        }
        //patternTrack = patternSequence.getTracks()[0];
        //numberOfMidiEvents = patternTrack.size();
        this.numberOfMidiEvents = organSequence.getEvents().size();
        this.isEmpty = false;
    }

    public OrganEvent getOrganEvent(int index) {
        return organSequence.getEvents().get(index);
    }

    /*
    public MidiEvent getMidiEvent(int index) {
        return patternTrack.get(index);
    }

    public MidiEvent getLastMidiEvent() {
        return patternTrack.get(numberOfMidiEvents - 1);
    }

     */

    public void setEvent(int index, OrganEvent event) {
        organSequence.getEvents().set(index, event);
    }


}
