package de.thws;

import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Start {
    public static void main(String[] args) throws Exception {

        KeyboardPool pool = new KeyboardPool(new File("sounds/new"));
        pool.setTempo(KeyboardPool.VERY_FAST);
        pool.getKeyboards().getFirst().makeActive();
        OrganSequencer sequencer = new OrganSequencer(pool);
        sequencer.start();
        InputTest test = new InputTest(pool, sequencer);
        test.start();


        /*
        Scanner sc = new Scanner(System.in);



        while(sequencer.isPlaying) {
            Thread.sleep(100);
            if (System.in.available() > 0) {
                String stringInput = sc.nextLine();
                if (stringInput.equals("+")) {
                    System.out.println("Entered +");
                    pool.keyboards.get(1).makeActive();
                }
                if (stringInput.equals("++")) {
                    System.out.println("Entered ++");
                    pool.keyboards.get(2).makeActive();
                }


            }

        }

         */

            //Thread.sleep(1000);


/*
        File firstManualDir = new File("sounds/new/first");
        File[] firsManualMidiFiles = firstManualDir.listFiles();

        Sequence[] sequencesFirstManual = new Sequence[firsManualMidiFiles.length];
        for (int i = 0; i < firsManualMidiFiles.length; i++) {
            sequencesFirstManual[i] = MidiSystem.getSequence(firsManualMidiFiles[i]);
        }

        List<Track[]> tracksFirstManual = new ArrayList<>();
        for (Sequence sequence : sequencesFirstManual) {
            tracksFirstManual.add(sequence.getTracks());
        }


        File secondManualDir = new File("sounds/new/second");
        File[] secondManualMidiFiles = secondManualDir.listFiles();

        Sequence[] sequencesSecondManual = new Sequence[secondManualMidiFiles.length];
        for (int i = 0; i < sequencesSecondManual.length; i++) {
            sequencesSecondManual[i] = MidiSystem.getSequence(secondManualMidiFiles[i]);
        }

        List<Track[]> tracksSecondManual = new ArrayList<>();
        int i=0;
        for (Sequence sequence : sequencesSecondManual) {
            tracksSecondManual.add(sequence.getTracks());
        }

        long tickLength = sequencesFirstManual[0].getTickLength();



        int resolution = sequencesFirstManual[0].getResolution(); // how many ticks in a quarter note

        var info = MidiSystem.getMidiDeviceInfo();
        MidiDevice outputDevice = MidiSystem.getMidiDevice(info[1]);


        float division = sequencesFirstManual[0].getDivisionType();

        long lengthInBeats = sequencesFirstManual[0].getTickLength() / ( sequencesFirstManual[0].getResolution() * 4L);

       // tracks[0].get(0).getMessage().

        outputDevice.open();


        Transmitter transmitter = outputDevice.getTransmitter();

        Synthesizer synth = MidiSystem.getSynthesizer();
        Receiver receiver = synth.getReceiver();
        synth.open();


        outputDevice.close();


        int firstManualEventIndex = 0;
        int secondManualEventIndex = 0;


        long currentSequenceLastIndex = 0L;
        int fileCounterFirstManual = 0;
        int fileCounterSecondManual = 0;
        boolean isPlaying = true;
        long ticks = 0;
        while (isPlaying) {
            Thread.sleep(1);
            MidiEvent firstManualEvent = null;
            MidiEvent secondManualEvent = null;
            try {
                firstManualEvent = tracksFirstManual
                        .get(fileCounterFirstManual)[0]
                        .get(firstManualEventIndex);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                if(fileCounterFirstManual == sequencesFirstManual.length - 1) {
                    firstManualEventIndex = 0;
                    fileCounterFirstManual = 0;
                    ticks = 0;
                    continue;
                    /*
                    isPlaying = false;
                    break;
                    

                }
                // no more events, get to next file
                currentSequenceLastIndex = tracksFirstManual.get(fileCounterFirstManual)[0].get(tracksFirstManual.get(fileCounterFirstManual)[0].size() - 1).getTick();
                ticks = ticks - currentSequenceLastIndex;
                fileCounterFirstManual++;
                firstManualEventIndex = 0;
                continue;
            }

            try {
                secondManualEvent = tracksSecondManual.get(fileCounterSecondManual)[0].get(secondManualEventIndex);
            }
            catch (ArrayIndexOutOfBoundsException exception) {
                if(fileCounterSecondManual == sequencesSecondManual.length - 1) {
                    /*
                    secondManualEventIndex = 0;
                    fileCounterSecondManual = 0;
                    ticks = 0;
                    continue;


                    isPlaying = false;
                    break;

                // todo hier funktioniert es nicht, schaue warum
                }
                // no more events, get to next file
                currentSequenceLastIndex = tracksFirstManual.get(fileCounterFirstManual)[0].get(tracksFirstManual.get(fileCounterFirstManual)[0].size() - 1).getTick();
                ticks = ticks - currentSequenceLastIndex;
                fileCounterSecondManual++;
                firstManualEventIndex = 0;
                continue;
            }

            if(firstManualEvent.getTick() <= ticks) {
                receiver.send(firstManualEvent.getMessage(), firstManualEvent.getTick());
                firstManualEventIndex++;
            }

            if(secondManualEvent.getTick() <= ticks) {
                receiver.send(secondManualEvent.getMessage(), secondManualEvent.getTick());
                secondManualEventIndex++;
            }

            ticks++;

        }




        synth.close();

       // Transmitter transmitter = MidiSystem.getTransmitter();

        outputDevice.close();






*/


        System.out.println("Hello world!");
    }
}