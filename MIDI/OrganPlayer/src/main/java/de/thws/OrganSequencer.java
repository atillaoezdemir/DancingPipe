package de.thws;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class OrganSequencer extends Thread {
    KeyboardPool keyboards;
    long beatLengthInTicks;
    Tempo tempoFactor;
    boolean isPlaying;

    public OrganSequencer(KeyboardPool keyboards) {
        super("OrganSequencer");
        this.keyboards = keyboards;
        isPlaying = false;
        this.tempoFactor = Tempo.NORMAL;
        this.beatLengthInTicks = (long) (keyboards.getBeatLengthInTicks() * this.tempoFactor.getValue());

    }

    @Override
    public void run() {
        isPlaying = true;

        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            Receiver receiver = synth.getReceiver();

            //long beatLengthInTicks = (long) (keyboards.getBeatLengthInTicks() * keyboards.getTempoFactor().getValue());

            int numberOfKeyboards = keyboards.getKeyboards().size();
            int[] currentPatternIndex = new int[numberOfKeyboards];
            int[] currentEventIndex = new int[numberOfKeyboards];

            synth.open();

            long maxTicksForKeyboard = getMaxTicksForKeyboardAfterTempoChange(0);

            // previous condition of the keyboard
            // all of the keyboards except the first one have previous condition inactive
            boolean[] previousCondition = new boolean[numberOfKeyboards];
            previousCondition[0] = true;

            // int patternIndex = 0;
            // int currentEventIndex = 0;
            Tempo previousTempoFactor = this.tempoFactor;
            long ticks = 0;
            long ticksSum = 0;
            while (isPlaying) {
                // synchronized ()
                // todo try with synchronized
                Thread.sleep(1);

                if (ticksSum >= maxTicksForKeyboard
                    // && ticksSum >= (beatLengthInTicks * 5L) * keyboards.getTempoFactor()
                ) //todo change to generic and fix continuing by slower tempo factor
                {
                    for (Keyboard keyboard : keyboards.getKeyboards()) {
                        for (int note : keyboard.getNotesOn()) {
                            ShortMessage sm = new ShortMessage(ShortMessage.NOTE_ON, note, 0);
                            receiver.send(sm, ticksSum);
                        }
                    }
                    isPlaying = false;
                }
                if (ticks >= beatLengthInTicks) {
                    // reset ticks to 0 and go to next pattern
                    ticks = 0;
                    Arrays.fill(currentEventIndex, 0);
                    for (int i = 0; i < currentPatternIndex.length; i++) {
                        int numberOfPatternsInKeyboard = keyboards.getKeyboards().get(i).getNumberOfPatterns();
                        if (currentPatternIndex[i] + 1 < numberOfPatternsInKeyboard) {
                            currentPatternIndex[i]++;
                        } else {
                            // no more patterns to play
                            currentPatternIndex[i] = -1;
                        }
                    }

                }
                if (previousTempoFactor != this.tempoFactor) {
                    System.out.println("tempo change");
                    // tempo was changed

                    if (previousTempoFactor.getValue() <this.tempoFactor.getValue()) {
                        // tempo was decreased
                        setTempoForPatterns(currentPatternIndex[0], false); // todo current pattern index should be the same for all
                        beatLengthInTicks = (long) (beatLengthInTicks * 1.25f);
                    } else {
                        setTempoForPatterns(currentPatternIndex[0], true);
                        beatLengthInTicks = (long) (beatLengthInTicks * 0.75f);
                    }
                    maxTicksForKeyboard = getMaxTicksForKeyboardAfterTempoChange(ticksSum);

                    // todo funktioniert noch nicht bei wenn man die Gesamtanzahl von beats berechnen muss
                    // todo mache, dass es erst im neuen pattern in Kraft tritt
                    previousTempoFactor = this.tempoFactor;
                }
                for (int keyboardIndex = 0; keyboardIndex < keyboards.getKeyboards().size(); keyboardIndex++) {
                    Keyboard currentKeyboard = keyboards.getKeyboards().get(keyboardIndex);
                    if (!currentKeyboard.isActive()) {
                        if (previousCondition[keyboardIndex]) {
                            // if keyboard was active, make all notes on the keyboard off
                            for (int note : currentKeyboard.getNotesOn()) {
                                ShortMessage sm = new ShortMessage(ShortMessage.NOTE_ON, note, 0);
                                receiver.send(sm, ticksSum);
                                previousCondition[keyboardIndex] = false;
                            }
                        }
                        continue;
                    }
                    previousCondition[keyboardIndex] = true;
                    if (currentPatternIndex[keyboardIndex] != -1) {
                        Pattern currentPattern = currentKeyboard
                                .getKeyboardPatterns()
                                .get(currentPatternIndex[keyboardIndex]);
                        if (currentPattern
                                .getOrganEvent(currentEventIndex[keyboardIndex])
                                .getTick() <= ticks
                                && currentEventIndex[keyboardIndex] < currentPattern.getNumberOfMidiEvents() - 1) {

                            OrganEvent currentEvent = currentPattern.getOrganEvent(currentEventIndex[keyboardIndex]);
                            //MidiEvent currentEvent = currentPattern.getMidiEvent(currentEventIndex[keyboardIndex]);
                            if (currentEvent.getMessage() instanceof ShortMessage sm) {
                                if (sm.getCommand() == ShortMessage.NOTE_ON) {
                                    keyboards.getKeyboards().get(keyboardIndex).addNoteToNotesOn(sm.getData1());
                                    System.out.println(sm.getData1());
                                }
                            }
                            receiver.send(currentEvent.getMessage(), currentEvent.getTick());

                            currentEventIndex[keyboardIndex]++;
                        }
                    }

                }
                // todo when making inactive all notes off or keep active until next noteoff event

                ticks++;
                ticksSum++;
            }
            synth.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void setTempo(int tempo) {
        if (isPlaying) {

        }
    }

    public void stopPLaying() {
        isPlaying = false;
    }

    long getMaxTicksForKeyboardAfterTempoChange(long currentTick) {
        long oldMaxTick = keyboards.getKeyboards()
                .stream()
                .map(Keyboard::getLastTick) // todo this should be in a separate variable //todo not general last tick but current last tick
                .toList()
                .stream()
                .mapToLong(value -> value)
                .max().orElseThrow(NoSuchElementException::new);

        Tempo tempoFactor = this.tempoFactor;

        long newMaxTick = keyboards.getKeyboards()
                .stream()
                .map(keyboard -> (long) (keyboard.updateLastTick())) //todo not general last tick but current last tick
                .toList()
                .stream()
                .mapToLong(value -> value)
                .max().orElseThrow(NoSuchElementException::new);

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
        return newMaxTick;
    }


    public void setTempoForPatterns(int index, boolean increase) {
        System.out.println(this.tempoFactor.name());
        keyboards.keyboards.forEach(keyboard -> {
            for (int i = index; i < keyboard.getNumberOfPatterns(); i++) {
                Pattern currentPattern = keyboard.getKeyboardPatterns().get(i);
                for (int ii = 0; ii < currentPattern.getNumberOfMidiEvents(); ii++) {
                    OrganEvent oldEvent = currentPattern.getOrganEvent(ii);
                    MidiMessage oldMessage = oldEvent.getMessage();
                    int factoredTick = (int) (increase ? oldEvent.getTick() * 0.75f : oldEvent.getTick() * 1.25f);
                    currentPattern.setEvent(ii, new OrganEvent(oldMessage, factoredTick));
                }

            }
        });


    }

    public void increaseTempo() {
        switch (this.tempoFactor) {
            case FASTER -> {
                this.tempoFactor = Tempo.FAST;
            }
            case NORMAL -> {
                this.tempoFactor = Tempo.FASTER;
            }
            case SLOWER -> {
                this.tempoFactor = Tempo.NORMAL;
            }
            case SLOW -> {
                this.tempoFactor = Tempo.SLOWER;
            }
        }
    }

    public void decreaseTempo() {
        switch (this.tempoFactor) {
            case FAST -> {
                this.tempoFactor = Tempo.FASTER;
            }
            case FASTER -> {
                this.tempoFactor = Tempo.NORMAL;
            }
            case NORMAL -> {
                this.tempoFactor = Tempo.SLOWER;
            }
            case SLOWER -> {
                this.tempoFactor = Tempo.SLOW;
            }
        }
    }

}
