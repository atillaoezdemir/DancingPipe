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

            int beatLengthInTicks = keyboards.getKeyboards().getFirst().getSequences().getFirst().getResolution() * 4;

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


            // int patternIndex = 0;
            // int currentEventIndex = 0;
            long ticks = 0;
            long ticksSum = 0;
            while (isPlaying) {
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
                        continue;
                    }
                    if (currentPatternIndex[keyboardIndex] != -1) {
                        Pattern currentPattern = currentKeyboard
                                .getKeyboardPatterns()
                                .get(currentPatternIndex[keyboardIndex]);
                        if (currentPattern
                                .getMidiEvent(currentEventIndex[keyboardIndex])
                                .getTick() <= ticks
                                && currentEventIndex[keyboardIndex] < currentPattern.getNumberOfMidiEvents() - 1) {

                            MidiEvent currentEvent = currentPattern.getMidiEvent(currentEventIndex[keyboardIndex]);
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
