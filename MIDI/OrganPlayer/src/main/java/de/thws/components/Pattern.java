package de.thws.components;

import de.thws.exceptions.OrganSequencerException;
import lombok.Getter;
import lombok.Setter;

import javax.sound.midi.*;
import java.io.File;

/**
 * Represents a single pattern, that will be played on the organ by the sequencer.
 * It is being loaded from a single MIDI file (See class {@link de.thws.configurators.PatternConfigurator}).
 * A pattern has a length of one bar and can also be empty (Long rest, see more <a href="https://en.wikipedia.org/wiki/List_of_musical_symbols#Rhythmic_values_of_notes_and_rests">here</a>).
 * <p><strong>Class members:</strong>
 * <ul>
 *     <li> {@code patternIndex} - indicates the current pattern number in the keyboard pattern list as {@code int}.
 *     <li> {@code canBeInterrupted} - {@code boolean} that indicates if the pattern can be interrupted in the sequencer.  <i style="color:gray;">Currently not used, left for future improvements</i>
 *     <li> {@code organSequence} - {@link OrganSequence} object that holds the MIDI events for the patterns.
 *     <li> {@code numberOfMidiEvents} - the number of MIDI events in the Pattern as {@code int}. If the pattern is empty, then the value is 0.
 *     <li> {@code patternName} - name of the MIDI file, from which the pattern is created, as {@link String}.
 *     <li> {@code empty} - {@code boolean} that indicates if the pattern is empty (long rest).
 * </ul>
 * @see de.thws.configurators.PatternConfigurator
 * @see OrganSequence
 */
@Setter
@Getter
public class Pattern {
    private int patternIndex;
    private boolean canBeInterrupted;
    private OrganSequence organSequence;
    private int numberOfMidiEvents;
    private final String patternName;
    private boolean empty;

    /**
     * Constructs a Pattern object, representing an empty pattern.
     */
    public Pattern() {
        this.empty = true;
        this.organSequence = null;
        this.numberOfMidiEvents = 0;
        this.patternName = "";

    }

    /**
     * Constructs a Pattern object from a MIDI file.
     * @param path path to the MIDI file for the pattern as {@link File}
     * @throws OrganSequencerException if the file cannot be read or found
     */
    public Pattern(File path) throws OrganSequencerException {
        this.patternName = path.getName();
        try {
            this.organSequence = new OrganSequence(MidiSystem.getSequence(path));
        }
        catch (Exception e) {
            if(e.getClass().getName().equals("InvalidMidiDataException")) {
                throw new OrganSequencerException("MidiUnavailableException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the MIDI files are not damaged.");
            }
            else if (e.getClass().getName().equals("IOException")) {
                throw new OrganSequencerException("IOException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the folder with the MIDI files is not empty and the MIDI files are not damaged.");
            }
            else if(e.getClass().getName().equals("de.thws.exceptions.OrganSequencerException")) {
                throw new OrganSequencerException(e.getMessage());
            }
            else {
                throw new OrganSequencerException("NullPointerException thrown when reading file '" + path.getAbsolutePath() + "'. \nPlease make sure that the right MIDI file is referenced or exists.");
            }
        }
        this.numberOfMidiEvents = organSequence.getEvents().size();
        this.empty = false;
    }

    public OrganEvent getOrganEvent(int index) {
        return organSequence.getEvents().get(index);
    }

    public void setEvent(int index, OrganEvent event) {
        organSequence.getEvents().set(index, event);
    }


}
