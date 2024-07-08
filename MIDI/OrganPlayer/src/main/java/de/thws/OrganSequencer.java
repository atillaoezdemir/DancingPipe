package de.thws;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class OrganSequencer extends Thread {
    KeyboardPool keyboards;
    boolean isPlaying;

    public OrganSequencer(KeyboardPool keyboards) {
        super("OrganSequencer");
        this.keyboards = keyboards;
        isPlaying = false;
    }

    @Override
    public void run() {
        isPlaying = true;

        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            Receiver receiver = synth.getReceiver();

            long beatLengthInTicks = (long) (keyboards.getBeatLengthInTicks() * keyboards.getTempoFactor().getValue());

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
            Tempo previousTempoFactor = keyboards.getTempoFactor();
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
                        for(int note : keyboard.getNotesOn()) {
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
                if(previousTempoFactor != keyboards.getTempoFactor()) {
                    System.out.println("tempo change");
                    // tempo was changed

                    maxTicksForKeyboard = getMaxTicksForKeyboardAfterTempoChange(ticksSum);
                    if(previousTempoFactor.getValue() < keyboards.getTempoFactor().getValue()) {
                        // tempo was decreased
                        keyboards.setTempoForPatterns(currentPatternIndex[0], false); // todo current pattern index should be the same for all
                        beatLengthInTicks = (long) (beatLengthInTicks * 1.25f);
                    }
                    else {
                        keyboards.setTempoForPatterns(currentPatternIndex[0], true);
                        beatLengthInTicks = (long) (beatLengthInTicks * 0.75f);
                    }

                    // todo funktioniert noch nicht bei wenn man die Gesamtanzahl von beats berechnen muss
                    // todo mache, dass es erst im neuen pattern in Kraft tritt
                    previousTempoFactor = keyboards.getTempoFactor();
                }
                for (int keyboardIndex = 0; keyboardIndex < keyboards.getKeyboards().size(); keyboardIndex++) {
                    Keyboard currentKeyboard = keyboards.getKeyboards().get(keyboardIndex);
                    if (!currentKeyboard.isActive()) {
                        if(previousCondition[keyboardIndex]) {
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
                            if(currentEvent.getMessage() instanceof ShortMessage sm) {
                                if(sm.getCommand() == ShortMessage.NOTE_ON) {
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
        }
        catch (Exception e) {
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

        Tempo tempoFactor = keyboards.getTempoFactor();

        long newMaxTick = keyboards.getKeyboards()
                .stream()
                .map(keyboard -> (long) (keyboard.getLastTick() * tempoFactor.getValue())) //todo not general last tick but current last tick
                .toList()
                .stream()
                .mapToLong(value -> value)
                .max().orElseThrow(NoSuchElementException::new);

        // how much of the whole sequence is elapsed
        float percentElapsed = (currentTick * 100f) /oldMaxTick;
        // if the whole sequence was in the new tempo, on which tick it would be now
        long currentTickInNewTempo = (long) (newMaxTick*percentElapsed/100);
        System.out.println(currentTick);
        System.out.println(currentTickInNewTempo);
        long remainingTicksInNewTempo = newMaxTick - currentTickInNewTempo;

        return currentTick + remainingTicksInNewTempo;
    }


}
