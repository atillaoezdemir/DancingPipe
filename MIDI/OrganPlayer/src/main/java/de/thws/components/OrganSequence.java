package de.thws.components;

import java.util.ArrayList;
import java.util.List;

import de.thws.exceptions.OrganSequencerException;
import lombok.Getter;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * Represents the content of a single MIDI file.
 * This class serves as a simplified version of the {@link Sequence} class, providing only the functionalities
 * necessary for the purpose of the application. It maintains a list of MIDI events as a {@link List} of {@link OrganEvent} objects.
 * Additionally, it stores information regarding the <a href="http://www.harfesoft.de/aixphysik/sound/midi/pages/miditmcn.html">resolution</a> of the MIDI file.
 * Object of this type can only be constructed from MIDI files that contain <b>one track</b>.
 * This class is used when creating a {@link Pattern} object.
 * <p><strong>Class members:</strong>
 * <ul>
 *     <li> {@code events} - {@link List} of {@link OrganEvent}s, used for saving the MIDI events in the sequence.
 *     <li> {@code resolution} - resolution of the sequence as {@code int}.
 * </ul>
 * @see Track
 * @see Sequence
 * @see OrganEvent
 */
@Getter
public class OrganSequence {

    private final List<OrganEvent> events;
    private final int resolution;

    /**
     * Constructs an {@link de.thws.OrganSequencer} object using a {@link Sequence} object.
     * @param sequence sequence to be used
     * @throws OrganSequencerException if the {@code sequence} has more than one MIDI track
     */
    public OrganSequence(Sequence sequence) throws OrganSequencerException {
        // if the sequence has more than one track throw exception
       int numberOfTracks = sequence.getTracks().length;
         if(numberOfTracks > 1) {
            throw new OrganSequencerException("The Sequence cannot have more than one track.");
        }
        this.events = new ArrayList<>();

        Track track = sequence.getTracks()[0];
        int numberOfMIDIEvents = track.size();
        for(int i = 0; i < numberOfMIDIEvents; i++) {
            this.events.add(new OrganEvent(track.get(i)));

        }
        this.resolution = sequence.getResolution();
    }


}
