package de.thws.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;

import lombok.Getter;

import java.util.List;

/**
 * Represents a single event in a pattern.
 * This class serves as simplifies version of the {@link MidiEvent} class, only the functionalities
 * necessary for the purpose of the application and direct access to the timestamp for the event.
 * <p><strong>Class members:</strong>
 * <ul>
 *     <li> {@code message} - the MIDI message contained in the event as {@link MidiMessage}.
 *     <li> {@code resolution} - the timestamp for the event, in MIDI ticks as {@code long}.
 * </ul>
 * @see MidiMessage
 * @see MidiEvent
 */
@Getter
public class OrganEvent {
    private final MidiMessage message; //
    private final long tick; //

    public OrganEvent(MidiMessage message, long tick) {
        this.message = message;
        this.tick = tick;
    }

    public OrganEvent(MidiEvent midiEvent) {
        this.message = midiEvent.getMessage();
        this.tick = midiEvent.getTick();
    }



}
