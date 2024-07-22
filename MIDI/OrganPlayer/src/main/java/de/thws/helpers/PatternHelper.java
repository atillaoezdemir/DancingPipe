package de.thws.helpers;

import de.thws.AppDetails;
import de.thws.components.OrganEvent;
import de.thws.components.OrganSequence;

import javax.sound.midi.ShortMessage;
import java.util.List;

/**
 * Contains methods used in the {@link de.thws.components.Pattern} class
 */
public class PatternHelper {

    /**
     * Deletes the last NoteOff event in the given sequence.
     * @param sequence sequence in which the last NoteOff event should be removed
     * @return true if the last NoteOff event was deleted, false otherwise
     */
    public static boolean deleteLastNoteOffEvent(OrganSequence sequence) {
        List<OrganEvent> events = sequence.getEvents();
        int sequenceIndex = events.size();

        while(--sequenceIndex >= 0) {
            if(events.get(sequenceIndex).getMessage() instanceof ShortMessage msg) {
                if(isNoteOffEvent(msg)) {
                    events.remove(sequenceIndex);
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Deletes the first NoteOn event in the given sequence.
     * @param sequence sequence in which the first NoteOn event should be removed
     * @return true if the first NoteOn event was deleted, false otherwise
     */
    public static boolean deleteFirstNoteOnEvent(OrganSequence sequence) {
        List<OrganEvent> events = sequence.getEvents();
        int sequenceSize = events.size();
        int sequenceIndex = -1;

        while(++sequenceIndex < sequenceSize) {
            if(events.get(sequenceIndex).getMessage() instanceof ShortMessage msg) {
                if(isNoteOnEvent(msg)) {
                    events.remove(sequenceIndex);
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean isNoteOffEvent(ShortMessage msg) {
        return (msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() == 0) || msg.getCommand() == ShortMessage.NOTE_OFF;
    }

    public static boolean isNoteEvent(ShortMessage msg) {
        return msg.getCommand() == ShortMessage.NOTE_ON || msg.getCommand() == ShortMessage.NOTE_OFF;
    }

    public static boolean isNoteOnEvent(ShortMessage msg) {
        return msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() != 0;
    }
}
