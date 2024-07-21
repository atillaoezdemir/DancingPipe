package de.thws;

import java.util.ArrayList;
import java.util.List;

import de.thws.exceptions.OrganSequencerException;
import lombok.Getter;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

@Getter
public class OrganSequence {

    private final List<OrganEvent> events;
    private final int resolution;
    private final float divisionType;

    public OrganSequence(List<OrganEvent> events, int resolution, float divisionType) {
        this.events = events;
        this.resolution = resolution;
        this.divisionType = divisionType;
    }

    public OrganSequence(Sequence sequence) throws OrganSequencerException {
        // if the sequence has more than one track throw exception
        int numberOfTracks = sequence.getTracks().length;
        if(numberOfTracks > 1) {
            throw new OrganSequencerException("The Sequence cannot have more than one track.");
        }

        this.events = new ArrayList<OrganEvent>();

        Track track = sequence.getTracks()[0];
        int numberOfMIDIEvents = track.size();
        for(int i = 0; i < numberOfMIDIEvents; i++) {
            this.events.add(new OrganEvent(track.get(i)));

        }


        this.resolution = sequence.getResolution();
        this.divisionType = sequence.getDivisionType();
    }


}
