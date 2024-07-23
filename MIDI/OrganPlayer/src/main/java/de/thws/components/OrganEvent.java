package de.thws.components;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import lombok.Getter;

@Getter
public class OrganEvent {
    private final MidiMessage message; // the MIDI message contained in the event
    private final long tick; // the time-stamp for the event, in MIDI ticks

    public OrganEvent(MidiMessage message, long tick) {
        this.message = message;
        this.tick = tick;
    }

    public OrganEvent(MidiEvent midiEvent) {
        this.message = midiEvent.getMessage();
        this.tick = midiEvent.getTick();
    }



}
