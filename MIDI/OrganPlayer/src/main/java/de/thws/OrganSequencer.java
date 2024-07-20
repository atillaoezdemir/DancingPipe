package de.thws;

import lombok.Getter;

import javax.sound.midi.*;
import java.util.*;
import de.thws.helpers.PatternHelper;

@Getter
public class OrganSequencer extends Thread {
    KeyboardPool keyboards;
    KeyboardPool keyboardPoolToUse;
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
    int[] channels = {1, 2, 3};


    public OrganSequencer(KeyboardPool keyboards, Receiver receiver) { // currently not in use
        super("OrganSequencer");
        this.keyboards = keyboards;
        isPlaying = false;
        this.currentTempo = Tempo.NORMAL;
        this.beatLengthInTicks = (long) (keyboards.getBeatLengthInTicks() * this.currentTempo.getValue());
        this.numberOfKeyboards = keyboards.keyboards.size();
        this.ticksSum = 0;
        this.receiver = receiver;
        this.lengthInTicks = keyboards.getKeyboards().getLast().getSequences().getLast().getEvents().getLast().getTick(); // todo not like this, read from file
    }

    /**
     * Constructor to use when creating a copy of the object
     */
    private OrganSequencer(KeyboardPool keyboards, long beatLengthInTicks, long lengthInTicks, Tempo currentTempo, float tempoFactor, boolean isPlaying, int numberOfKeyboards, long ticksSum, Receiver receiver, int[] channels) {
        super("OrganSequencer");
        this.keyboards = keyboards;
        this.beatLengthInTicks = beatLengthInTicks;
        this.lengthInTicks = lengthInTicks;
        this.currentTempo = currentTempo;
        this.tempoFactor = tempoFactor;
        this.isPlaying = isPlaying;
        this.numberOfKeyboards = numberOfKeyboards;
        this.ticksSum = ticksSum;
        this.receiver = receiver;
        this.channels = channels;
    }

    public OrganSequencer(Composition composition, Receiver receiver) {
        super("OrganSequencer");
        this.keyboards = composition.getKeyboardPool();
        this.keyboardPoolToUse = null;
        this.numberOfKeyboards = keyboards.keyboards.size();

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

    private OrganSequencer copy() {
        return new OrganSequencer(this.keyboards, this.beatLengthInTicks, this.lengthInTicks, this.currentTempo, this.tempoFactor, this.isPlaying, this.numberOfKeyboards, this.ticksSum, this.receiver, this.channels);
    }

    @Override
    public void run() {
        System.out.println("Thread started");
        startPlaying();
        System.out.println("Thread terminated");
    }


    public void setTempo(int tempo) {
        if (isPlaying) {

        }
    }

    public void stopPlaying() throws InvalidMidiDataException {
        isPlaying = false;
        sendNoteOffToAllPlayingNotes();
        //resetFields();
        // todo reset everything
    }

    long getMaxTicksForKeyboardAfterTempoChange(long currentTick) {

        /*
        // how much of the whole sequence is elapsed
        float percentElapsed = (currentTick * 100f) / oldMaxTick;
        // if the whole sequence was in the new tempo, on which tick it would be now
        long currentTickInNewTempo = (long) (newMaxTick * percentElapsed / 100);
        System.out.println(currentTick);
        System.out.println(currentTickInNewTempo);
        long remainingTicksInNewTempo = newMaxTick - currentTickInNewTempo;

         */

        //return currentTick + remainingTicksInNewTempo;
        return keyboards.getKeyboards()
                .stream()
                .map(keyboard -> (long) (keyboard.updateLastTick())) //todo not general last tick but current last tick
                .toList()
                .stream()
                .mapToLong(value -> value)
                .max().orElseThrow(NoSuchElementException::new);
    }


    public void setTempoForPatterns(int index, boolean increase) {

        System.out.println(this.currentTempo.name());
        keyboards.keyboards.forEach(keyboard -> {
            for (int i = index; i < keyboard.getNumberOfPatterns(); i++) {
                Pattern currentPattern = keyboard.getKeyboardPatterns().get(i);
                for (int ii = 0; ii < currentPattern.getNumberOfMidiEvents(); ii++) {
                    OrganEvent oldEvent = currentPattern.getOrganEvent(ii);
                    MidiMessage oldMessage = oldEvent.getMessage();
                    int factoredTick = (int) (increase ? oldEvent.getTick() * this.tempoIncreaseFactor : oldEvent.getTick() * this.tempoDecreaseFactor);
                    currentPattern.setEvent(ii, new OrganEvent(oldMessage, factoredTick));
                }

            }
        });


    }

    public void increaseTempo() {
        switch (this.currentTempo) {
            case FASTER -> {
                this.currentTempo = Tempo.FAST;
            }
            case NORMAL -> {
                this.currentTempo = Tempo.FASTER;
            }
            case SLOWER -> {
                this.currentTempo = Tempo.NORMAL;
            }
            case SLOW -> {
                this.currentTempo = Tempo.SLOWER;
            }
        }
    }

    public void decreaseTempo() {
        switch (this.currentTempo) {
            case FAST -> {
                this.currentTempo = Tempo.FASTER;
            }
            case FASTER -> {
                this.currentTempo = Tempo.NORMAL;
            }
            case NORMAL -> {
                this.currentTempo = Tempo.SLOWER;
            }
            case SLOWER -> {
                this.currentTempo = Tempo.SLOW;
            }
        }
    }

    public void setTempoDoDefault() {
        this.currentTempo = Tempo.NORMAL;
    }

    public int getKeyboardsInUse() {
        int result = 0;
        for(Keyboard keyboard : this.keyboardPoolToUse.getKeyboards()) {
            if(keyboard.isActive()) {
                result++;
            }
        }
        return result;
    }

    public void incrementKeyboards() {
        int keyboardIndex = 0;
        for(keyboardIndex = 0; keyboardIndex < this.keyboardPoolToUse.getKeyboards().size(); keyboardIndex++) {
            if(!this.keyboardPoolToUse.getKeyboards().get(keyboardIndex).isActive()) {
                break;
            }
        }
        if(keyboardIndex < this.keyboardPoolToUse.getKeyboards().size()) {
            this.keyboardPoolToUse.keyboards.get(keyboardIndex).makeActive();
        }
    }

    public void decrementKeyboards() {
        int keyboardIndex;
        for(keyboardIndex = 0; keyboardIndex < this.keyboardPoolToUse.getKeyboards().size(); keyboardIndex++) {
            if(!this.keyboardPoolToUse.getKeyboards().get(keyboardIndex).isActive()) {
                break;
            }
        }
        if(keyboardIndex != 1) {
            keyboardIndex--;
            this.keyboardPoolToUse.getKeyboards().get(keyboardIndex).makeInactive();
        }

    }

    public void setKeyboardsToMax() {
        for(Keyboard keyboard : this.keyboardPoolToUse.getKeyboards()) {
            keyboard.makeActive();
        }
    }

    public void setKeyboardsToMin() {
        for(int i=1; i<this.keyboardPoolToUse.getKeyboards().size(); i++) {
            this.keyboardPoolToUse.keyboards.get(i).makeInactive();
        }
    }

    public void startPlaying() {
        // Basic idea of the sequencer:
        // Each ms iterate over all the manuals and all the patterns in the manual
        // and send MIDI signal if the manual is active

        // create copies of some of the class members, so that the originals can remain unchanged
        // for the next time the sequencer is started
        this.keyboardPoolToUse = this.keyboards;
        long currSeqLenInTicks = this.lengthInTicks;
        long currBeatLenInTicks = this.beatLengthInTicks;


        int[] currentPatternIndex = new int[this.numberOfKeyboards]; //todo obsolete to be array // used to track which pattern for each keyboard is currently playing
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
        keyboardPoolToUse.getKeyboards().getFirst().makeActive();

        //todo change copy values instead of the original ones
        this.isPlaying = true;
        try {


/*
            MidiDevice.Info outputDevice = Arrays.stream(MidiSystem.getMidiDeviceInfo()).toList().get(4);
            MidiDevice virtualOutPort = MidiSystem.getMidiDevice(outputDevice); //out

            virtualOutPort.open();

            Receiver receiver = virtualOutPort.getReceiver();


 */


/*
            Synthesizer synth = MidiSystem.getSynthesizer(); //if you use that again don't forget synth.open and synth.close
            Receiver receiver = synth.getReceiver();
 */

            //long beatLengthInTicks = (long) (keyboards.getBeatLengthInTicks() * keyboards.getTempoFactor().getValue());



            // synth.open();




            // int patternIndex = 0;
            // int currentEventIndex = 0;

            while (isPlaying) {
                // synchronized ()
                // todo try with synchronized
                Thread.sleep(1);

                if (ticksSum >= currSeqLenInTicks) { // stop playing if reached end of the composition
                    stopPlaying();
                }
                if (ticks >= currBeatLenInTicks) { // todo make generic for pattern length (not only for one beat)
                    // reset ticks to 0 and go to next pattern
                    ticks = 0;
                    Arrays.fill(currentEventIndex, 0);
                    for (int i = 0; i < currentPatternIndex.length; i++) {
                        int numberOfPatternsInKeyboard = keyboardPoolToUse.getKeyboards().get(i).getNumberOfPatterns();
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
                    System.out.println("tempo change");
                    // tempo was changed

                    if (previousTempoFactor.getValue() <this.currentTempo.getValue()) {
                        // tempo was decreased
                        setTempoForPatterns(currentPatternIndex[0], false); // todo current pattern index should be the same for all
                        currBeatLenInTicks = (long) (currBeatLenInTicks * this.tempoDecreaseFactor);
                        currSeqLenInTicks = (long) (currSeqLenInTicks * this.tempoDecreaseFactor);
                    } else {
                        setTempoForPatterns(currentPatternIndex[0], true);
                        currBeatLenInTicks = (long) (currBeatLenInTicks * this.tempoIncreaseFactor);
                        currSeqLenInTicks = (long) (currSeqLenInTicks * this.tempoIncreaseFactor);

                    }

                    // todo mache, dass es erst im neuen pattern in Kraft tritt
                    previousTempoFactor = this.currentTempo;
                }
                for (int keyboardIndex = 0; keyboardIndex < keyboardPoolToUse.getKeyboards().size(); keyboardIndex++) {
                    Keyboard currentKeyboard = keyboardPoolToUse.getKeyboards().get(keyboardIndex);
                    if (!currentKeyboard.isActive()) {
                        if (previousCondition[keyboardIndex]) {
                            // if keyboard was active, make all notes on the keyboard off

                            sendNoteOffToAllPlayingNotesOnKeyboard(currentKeyboard, keyboardIndex);

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
                                    if(PatternHelper.isNoteOffEvent(sm)) {
                                        sm.setMessage(ShortMessage.NOTE_OFF, channels[keyboardIndex], sm.getData1(), sm.getData2());
                                    }
                                    else {
                                        sm.setMessage(ShortMessage.NOTE_ON, channels[keyboardIndex], sm.getData1(), sm.getData2());
                                        keyboardPoolToUse.getKeyboards().get(keyboardIndex).addNoteToNotesOn(sm.getData1());

                                    }
                                    receiver.send(currentEvent.getMessage(), currentEvent.getTick());

                                }
                            }

                            currentEventIndex[keyboardIndex]++;
                        }
                    }

                }
                // todo when making inactive all notes off or keep active until next noteoff event

                ticks++;
                ticksSum++;
            }
            // synth.close();
        } catch (Exception e) {
            e.printStackTrace(); //todo make error catching better
        }
        resetFields();
        // todo try with wait and sleep for the thread
    }

    private void sendNoteOffToAllPlayingNotesOnKeyboard(Keyboard keyboard, int keyboardIndex) throws InvalidMidiDataException { // todo each keyboard to save the channel in the Keyboard class, here keyboardIndex will be obsolete
        for(int note : keyboard.getNotesOn()) {
            ShortMessage sm = new ShortMessage(ShortMessage.NOTE_OFF, channels[keyboardIndex], note, 0); // short message with chanels
            receiver.send(sm, ticksSum);

        }
    }



    private void sendNoteOffToAllPlayingNotes() throws InvalidMidiDataException {
        for(int i=0; i<keyboards.getKeyboards().size(); i++) {
            sendNoteOffToAllPlayingNotesOnKeyboard(keyboards.getKeyboards().get(i), i);
        }
    }

    private void resetFields() {
        this.currentTempo = Tempo.NORMAL;
        this.isPlaying = false;
        this.ticksSum = 0;
        this.keyboardPoolToUse = null;
    }


}
