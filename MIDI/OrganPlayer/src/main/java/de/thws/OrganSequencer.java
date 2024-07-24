package de.thws;

import com.diogonunes.jcolor.Attribute;
import de.thws.components.*;
import de.thws.enums.Tempo;
import de.thws.helpers.AppDetailsHelper;
import lombok.Getter;

import javax.sound.midi.*;
import java.util.*;

import de.thws.helpers.PatternHelper;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * This class represents a MIDI Sequencer for sequentially playing patterns on multiple keyboards. Users can add or remove patterns and keyboards, as well as change the sequence tempo.
 * The sequencer is designed to play a single composition at a time and cannot be paused once it has started. If stopped, it can only be restarted from the beginning with the default tempo and number of keyboards.
 * The current version of the sequencer only accepts Patterns with <b>length of one bar</b> and compositions in <b>4/4 signature</b>
 *
 * <p>This class extends {@link Thread}, enabling multithreading capabilities to allow real-time user input during sequence playback.</p>
 *
 * <p><strong>Class Members:</strong>
 * <ul>
 *     <li>{@code keyboards} - An instance of {@link KeyboardPool} that contains all the keyboards and their respective patterns.</li>
 *     <li>{@code beatLengthInTicks} - The length of one beat in MIDI ticks, represented as a {@code long}. <i>More information on MIDI ticks can be found <a href="https://www.recordingblogs.com/wiki/midi-tick">here</a>.</i></li>
 *     <li>{@code currentTempo} - The current tempo of the composition, represented as a {@link Tempo} value. Default tempo is {@code NORMAL}. Other possible values are {@code VERY_FAST}, {@code FAST}, {@code SLOW}, and {@code VERY_SLOW}.</li>
 *     <li>{@code tempoFactor} - A {@code float} value representing the tempo factor used for changing the composition's tempo. <i>Typically, this value ranges between 0.1 and 0.5.</i></li>
 *     <li>{@code tempoIncreaseFactor} - A factor used to multiply the MIDI events in the sequence when increasing the tempo. This value is calculated as {@code 1 - tempoFactor}.</li>
 *     <li>{@code tempoDecreaseFactor} - A factor used to multiply the MIDI events in the sequence when decreasing the tempo. This value is calculated as {@code 1 + tempoFactor}.</li>
 *     <li>{@code isPlaying} - A {@code boolean} indicating whether the sequencer is currently running. {@code true} if running, {@code false} otherwise.</li>
 *     <li>{@code numberOfKeyboards} - An {@code int} representing the number of keyboards used in the sequence. This value is derived from the {@code keyboards} attribute.</li>
 *     <li>{@code ticksSum} - The current number of ticks accumulated while the sequencer is running, used to determine the actual position in the sequence. The default value is {@code 0}.</li>
 *     <li>{@code receiver} - The MIDI device to which the MIDI signals are sent, implementing the {@link Receiver} interface.</li>
 * </ul>
 *
 * @see Receiver
 * @see MidiDevice
 * @see MidiMessage
 * @see ShortMessage
 */
@Getter
public class OrganSequencer extends Thread {
    KeyboardPool keyboards;
    long beatLengthInTicks;
    long lengthInTicks;
    Tempo currentTempo;
    float tempoFactor;
    float tempoIncreaseFactor;
    float tempoDecreaseFactor;

    boolean isPlaying;
    int numberOfKeyboards;
    long ticksSum;
    Receiver receiver;

    /**
     * Constructs an instance of {@link OrganSequencer} using the specified {@link Composition} and {@link Receiver}.
     *
     * @param composition The {@link Composition} object that provides the sequence of patterns to be played by the sequencer.
     * @param receiver    The {@link Receiver} object representing the MIDI device to which all MIDI signals will be sent.
     */
    public OrganSequencer(Composition composition, Receiver receiver) {
        super("OrganSequencer");
        this.keyboards = composition.getKeyboardPool();

        this.numberOfKeyboards = keyboards.getKeyboards().size();

        this.currentTempo = Tempo.NORMAL;
        this.tempoFactor = composition.getTempoFactor();
        this.tempoIncreaseFactor = 1 - this.tempoFactor;
        this.tempoDecreaseFactor = 1 + this.tempoFactor;

        this.beatLengthInTicks = keyboards.getBeatLengthInTicks();
        this.lengthInTicks = this.beatLengthInTicks * composition.getLengthInBars();

        this.receiver = receiver;

        this.ticksSum = 0;
        this.isPlaying = false;
    }

    @Override
    public void run() {
        startPlaying();
        System.out.println(colorize("Sequencer stopped!", Attribute.BLUE_BACK(), Attribute.BLACK_TEXT()));
    }

    public void stopPlaying() throws InvalidMidiDataException {
        isPlaying = false;
        sendNoteOffToAllPlayingNotes();
    }

    /**
     * Adjusts the tempo for all MIDI patterns beginning from the specified index.
     * The distance between MIDI messages in each pattern is altered to either increase or decrease the tempo.
     *
     * @param index    The starting index in each keyboard's pattern list from which the tempo adjustment will begin.
     * @param increase A boolean flag indicating whether to increase ({@code true}) or decrease ({@code false}) the tempo.
     */
    public void setTempoForPatterns(int index, boolean increase) {
        keyboards.getKeyboards().forEach(keyboard -> {
            if (index != -1) {
                for (int i = index; i < keyboard.getNumberOfPatterns(); i++) {
                    Pattern currentPattern = keyboard.getKeyboardPatterns().get(i);
                    for (int ii = 0; ii < currentPattern.getNumberOfMidiEvents(); ii++) {
                        OrganEvent oldEvent = currentPattern.getOrganEvent(ii);
                        MidiMessage oldMessage = oldEvent.getMessage();
                        int factoredTick = (int) (increase ? oldEvent.getTick() * this.tempoIncreaseFactor : oldEvent.getTick() * this.tempoDecreaseFactor); // updated timestamp for event after tempo change
                        currentPattern.setEvent(ii, new OrganEvent(oldMessage, factoredTick));
                    }

                }
            }

        });
    }


    /**
     * Increases the tempo of the sequencer and updates the {@code currentTempo} parameter accordingly.
     * The tempo transitions through the following sequence:
     * <ul>
     *     <li>{@code VERY_SLOW} -> {@code SLOW}</li>
     *     <li>{@code SLOW} -> {@code NORMAL}</li>
     *     <li>{@code NORMAL} -> {@code FAST}</li>
     *     <li>{@code FAST} -> {@code VERY_FAST}</li>
     * </ul>
     */
    public void increaseTempo() {
        switch (this.currentTempo) {
            case FAST -> this.currentTempo = Tempo.VERY_FAST;

            case NORMAL -> this.currentTempo = Tempo.FAST;

            case SLOW -> this.currentTempo = Tempo.NORMAL;

            case VERY_SLOW -> this.currentTempo = Tempo.SLOW;

        }
    }

    /**
     * Decreases the tempo of the sequencer and updates the {@code currentTempo} parameter accordingly.
     * The tempo transitions through the following sequence:
     * <ul>
     *     <li>{@code VERY_FAST} -> {@code FAST}</li>
     *     <li>{@code FAST} -> {@code NORMAL}</li>
     *     <li>{@code NORMAL} -> {@code SLOW}</li>
     *     <li>{@code SLOW} -> {@code VERY_SLOW}</li>
     * </ul>
     */
    public void decreaseTempo() {
        switch (this.currentTempo) {
            case VERY_FAST -> this.currentTempo = Tempo.FAST;

            case FAST -> this.currentTempo = Tempo.NORMAL;

            case NORMAL -> this.currentTempo = Tempo.SLOW;

            case SLOW -> this.currentTempo = Tempo.VERY_SLOW;

        }
    }

    /**
     * Resets the tempo of the sequencer to the default value and sets the {@code currentTempo} parameter to {@code NORMAL}.
     */
    public void setTempoToDefault() {
        this.currentTempo = Tempo.NORMAL;
    }

    /**
     * Counts the number of active keyboards in the sequencer.
     *
     * @return The number of active keyboards as an {@code int}.
     */
    public int getKeyboardsInUse() {
        int result = 0;
        for (Keyboard keyboard : this.keyboards.getKeyboards()) {
            if (keyboard.isActive()) {
                result++;
            }
        }
        return result;
    }

    /**
     * Activates the next first inactive keyboard if one exists.
     * If all keyboards are already active, the number of active keyboards remains unchanged.
     */
    public void incrementKeyboards() {
        int keyboardIndex = 0;
        for (keyboardIndex = 0; keyboardIndex < this.keyboards.getKeyboards().size(); keyboardIndex++) {
            if (!this.keyboards.getKeyboards().get(keyboardIndex).isActive()) {
                break;
            }
        }
        if (keyboardIndex < this.keyboards.getKeyboards().size()) {
            this.keyboards.getKeyboards().get(keyboardIndex).makeActive();
        }
    }

    /**
     * Deactivates the next first active keyboard, except the first one.
     * If only the first keyboard is active, the number of active keyboards remains unchanged.
     */
    public void decrementKeyboards() {
        int keyboardIndex;
        for (keyboardIndex = 0; keyboardIndex < this.keyboards.getKeyboards().size(); keyboardIndex++) {
            if (!this.keyboards.getKeyboards().get(keyboardIndex).isActive()) {
                break;
            }
        }
        if (keyboardIndex != 1) {
            keyboardIndex--;
            this.keyboards.getKeyboards().get(keyboardIndex).makeInactive();
        }

    }

    /**
     * Activates all keyboards in the sequencer.
     */
    public void setKeyboardsToMax() {
        for (Keyboard keyboard : this.keyboards.getKeyboards()) {
            keyboard.makeActive();
        }
    }

    /**
     * Deactivates all keyboards except the first one.
     */
    public void setKeyboardsToMin() {
        for (int i = 1; i < this.keyboards.getKeyboards().size(); i++) {
            this.keyboards.getKeyboards().get(i).makeInactive();
        }
    }

    /**
     * Starts the sequencer, iterating over all keyboards and their patterns to send MIDI signals in real-time.
     * The sequencer operates as follows:
     * <ul>
     *     <li>Each millisecond, it iterates over all keyboards and their patterns, sending MIDI signals if the keyboard is active.</li>
     *     <li>Keeps track of the current pattern and event indices for each keyboard.</li>
     *     <li>Monitors tempo changes and adjusts the timing of MIDI events accordingly.</li>
     *     <li>Handles keyboard activation and deactivation, sending appropriate MIDI messages.</li>
     *     <li>Stops automatically when the end of the composition is reached, if the user requests it, or if an error occurs.</li>
     * </ul>
     */
    public void startPlaying() {
        // Basic idea of the sequencer:
        // Each ms iterate over all the manuals and all the patterns in the manual
        // and send MIDI signal if the manual is active

        // create copies of some of the class members, so that the originals can remain unchanged
        // for the next time the sequencer is started
        long currSeqLenInTicks = this.lengthInTicks;
        long currBeatLenInTicks = this.beatLengthInTicks;

        int[] currentPatternIndex = new int[this.numberOfKeyboards]; // used to track which pattern for each keyboard is currently playing
        int[] currentEventIndex = new int[this.numberOfKeyboards]; // used to track which MIDI-Event for each pattern currently read

        // previous condition of the keyboard is used to track changes in the keyboards
        // all the keyboards except the first one have previous condition inactive at the beginning
        boolean[] previousCondition = new boolean[numberOfKeyboards];
        previousCondition[0] = true;

        // previous tempo factor is used to track changes in the tempo,
        // at the beginning set to the current tempo
        Tempo previousTempoFactor = this.currentTempo;
        long ticks = 0;

        // make first keyboard active
        keyboards.getKeyboards().getFirst().makeActive();

        this.isPlaying = true;
        try {
            while (isPlaying) {
                Thread.sleep(1);

                if (ticksSum >= currSeqLenInTicks) { // stop playing if reached end of the composition
                    System.out.println(colorize("Composition ended!", Attribute.BLUE_BACK(), Attribute.BLACK_TEXT(), Attribute.BOLD()));
                    stopPlaying();
                }
                if (ticks >= currBeatLenInTicks) {
                    sendNoteOffToAllPlayingNotes();
                    // go to next beat (respectively next pattern)
                    // reset ticks to 0 and go to next pattern
                    ticks = 0;
                    Arrays.fill(currentEventIndex, 0);
                    for (int i = 0; i < numberOfKeyboards; i++) {
                        int numberOfPatternsInKeyboard = keyboards.getKeyboards().get(i).getNumberOfPatterns();
                        if (currentPatternIndex[i] + 1 < numberOfPatternsInKeyboard) {
                            currentPatternIndex[i]++;
                        } else {
                            // no more patterns to play
                            currentPatternIndex[i] = -1;
                        }
                    }
                }
                // detect tempo changes
                if (previousTempoFactor != this.currentTempo) {
                    // tempo was changed

                    if (previousTempoFactor.getValue() < this.currentTempo.getValue()) {
                        // tempo was decreased
                        int iterations = this.currentTempo.getValue() - previousTempoFactor.getValue(); // if tempo was previously very fast and now normal, two iterations are needed
                        for (int i = 0; i < iterations; i++) {
                            setTempoForPatterns(currentPatternIndex[0], false); // take index from keyboard that is always playing
                        }
                        currBeatLenInTicks = (long) (currBeatLenInTicks * this.tempoDecreaseFactor);
                        currSeqLenInTicks = (long) (ticksSum + (currSeqLenInTicks - ticksSum) * this.tempoDecreaseFactor); // calculate corrected length of the sequence

                    } else {
                        // tempo was increased
                        int iterations = previousTempoFactor.getValue() - this.currentTempo.getValue(); // if tempo was previously very slow and now normal, two iterations are needed
                        for (int i = 0; i < iterations; i++) {
                            setTempoForPatterns(currentPatternIndex[0], true);
                        }
                        currBeatLenInTicks = (long) (currBeatLenInTicks * this.tempoIncreaseFactor);
                        currSeqLenInTicks = (long) (ticksSum + (currSeqLenInTicks - ticksSum) * this.tempoIncreaseFactor); // calculate corrected length of the sequence

                    }
                    previousTempoFactor = this.currentTempo;
                }
                for (int keyboardIndex = 0; keyboardIndex < keyboards.getKeyboards().size(); keyboardIndex++) {
                    Keyboard currentKeyboard = keyboards.getKeyboards().get(keyboardIndex);
                    if (!currentKeyboard.isActive()) {
                        if (previousCondition[keyboardIndex]) {
                            // if keyboard was active, make all notes on the keyboard off

                            sendNoteOffToAllPlayingNotesOnKeyboard(currentKeyboard);

                            previousCondition[keyboardIndex] = false;
                        }
                        continue;
                    }
                    previousCondition[keyboardIndex] = true;
                    if (currentPatternIndex[keyboardIndex] != -1) {
                        Pattern currentPattern = currentKeyboard
                                .getKeyboardPatterns()
                                .get(currentPatternIndex[keyboardIndex]);
                        if (!currentPattern.isEmpty() && currentPattern
                                .getOrganEvent(currentEventIndex[keyboardIndex])
                                .getTick() <= ticks
                                && currentEventIndex[keyboardIndex] < currentPattern.getNumberOfMidiEvents() - 1) {

                            OrganEvent currentEvent = currentPattern.getOrganEvent(currentEventIndex[keyboardIndex]);
                            //MidiEvent currentEvent = currentPattern.getMidiEvent(currentEventIndex[keyboardIndex]);
                            if (currentEvent.getMessage() instanceof ShortMessage sm) {
                                if (PatternHelper.isNoteEvent(sm)) {
                                    if (PatternHelper.isNoteOffEvent(sm)) {
                                        sm.setMessage(ShortMessage.NOTE_OFF, currentKeyboard.getKeyboardName().getChannelNumber(), sm.getData1(), sm.getData2());
                                    } else {
                                        sm.setMessage(ShortMessage.NOTE_ON, currentKeyboard.getKeyboardName().getChannelNumber(), sm.getData1(), sm.getData2());
                                        keyboards.getKeyboards().get(keyboardIndex).addNoteToNotesOn(sm.getData1());

                                    }
                                    receiver.send(currentEvent.getMessage(), currentEvent.getTick());

                                }
                            }
                            currentEventIndex[keyboardIndex]++;
                        }
                    }

                }
                ticks++;
                ticksSum++;
            }
        } catch (InvalidMidiDataException e) {
            AppDetailsHelper.displayErrorMessage("InvalidMidiDataException thrown! Make sure that the data is valid!\n" + e.getMessage());
            isPlaying = false;
        } catch (InterruptedException e) {
            AppDetailsHelper.displayErrorMessage("InterruptedException thrown!\n" + e.getMessage());
            isPlaying = false;
        }
    }

    /**
     * Sends {@code NOTE_OFF} message to all notes that are playing on the {@code keyboard}
     *
     * @param keyboard keyboard on which the {@code NOTE_OFF} message should be sent
     * @throws InvalidMidiDataException if the {@code NOTE_OFF} message is invalid
     */
    private void sendNoteOffToAllPlayingNotesOnKeyboard(Keyboard keyboard) throws InvalidMidiDataException {
        for (int note : keyboard.getNotesOn()) {
            ShortMessage sm = new ShortMessage(ShortMessage.NOTE_OFF, keyboard.getKeyboardName().getChannelNumber(), note, 0); // short message with channels
            receiver.send(sm, ticksSum);

        }
    }

    /**
     * Sends {@code NOTE_OFF} message to all notes that are playing on all keyboards
     *
     * @throws InvalidMidiDataException if the {@code NOTE_OFF} message is invalid
     */
    private void sendNoteOffToAllPlayingNotes() throws InvalidMidiDataException {
        for (int i = 0; i < keyboards.getKeyboards().size(); i++) {
            sendNoteOffToAllPlayingNotesOnKeyboard(keyboards.getKeyboards().get(i));
        }
    }

    private void resetFields() {
        this.currentTempo = Tempo.NORMAL;
        this.isPlaying = false;
        this.ticksSum = 0;
    }


}
