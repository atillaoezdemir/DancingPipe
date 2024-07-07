package de.thws;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

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

            long beatLengthInTicks = keyboards.getBeatLengthInTicks();

            int numberOfKeyboards = keyboards.getKeyboards().size();
            int[] currentPatternIndex = new int[numberOfKeyboards];
            int[] currentEventIndex = new int[numberOfKeyboards];


            synth.open();

            long maxTicksForKeyboard = keyboards.getKeyboards()
                    .stream()
                    .map(Keyboard::getLastTick)
                    .toList()
                    .stream()
                    .mapToLong(value -> value)
                    .max().orElseThrow(NoSuchElementException::new);


            // previous condition of the keyboard
            // all of the keyboards except the first one have previous condition inactive
            boolean[] previousCondition = new boolean[numberOfKeyboards];
            previousCondition[0] = true;

            // int patternIndex = 0;
            // int currentEventIndex = 0;
            long ticks = 0;
            long ticksSum = 0;
            while (isPlaying) {
                // synchronized ()
                // todo try with synchronized
                Thread.sleep(1);

                if (ticksSum >= maxTicksForKeyboard && ticksSum >= beatLengthInTicks * 5L + 500) //todo change to generic
                {
                    isPlaying = false;
                }
                if (ticks >= beatLengthInTicks) {
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
                                .getMidiEvent(currentEventIndex[keyboardIndex])
                                .getTick() <= ticks
                                && currentEventIndex[keyboardIndex] < currentPattern.getNumberOfMidiEvents() - 1) {

                            OrganEvent currentEvent = currentPattern.getMidiEvent(currentEventIndex[keyboardIndex]);
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
}
