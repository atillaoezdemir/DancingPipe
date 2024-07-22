package de.thws;

import de.thws.client.v2.ConsumerTestClient;
import de.thws.exceptions.MenuExitException;
import de.thws.helpers.AppDetailsHelper;
import de.thws.pickers.CompositionPicker;
import de.thws.pickers.ModePicker;
import de.thws.pickers.OutputDevicePicker;

import javax.sound.midi.*;

/**
 * Starting point for the application. Use this class to start the application.
 */
public class Start {
    public static void main(String[] args) throws MidiUnavailableException { // todo handle exception

        // todo test if empty bars work

        AppDetails.displayDetails();

        try {
            final MidiDevice.Info outputDeviceInfo = OutputDevicePicker.chooseDevice();
            MidiDevice outputDevice = null;
            try {
                outputDevice = MidiSystem.getMidiDevice(outputDeviceInfo);
            } catch (MidiUnavailableException e) {
                System.out.println("Device " + outputDeviceInfo.getName() + " not available. This device could be currently in use by another application:");
            }

            if (outputDevice != null) {
                outputDevice.open();
                Receiver receiver = outputDevice.getReceiver();

                CompositionPicker cp = new CompositionPicker("sounds");

                String compositionPath = cp.pickComposition();
                if (!compositionPath.isEmpty()) {
                    boolean mode = ModePicker.pickMode();
                    try {
                        if (mode) {
                            ConsumerTestClient client = new ConsumerTestClient(receiver, compositionPath);
                            client.start();
                            client.join();
                        } else {
                            InputTest inputTest = new InputTest(receiver, compositionPath);
                            inputTest.start();
                            inputTest.join();
                        }
                    }
                    catch(InterruptedException e){
                        System.out.println("Interrupted Exception thrown: " + e.getMessage());
                        throw new MenuExitException("");
                    }
                    throw new MenuExitException("");
                } else throw new MenuExitException("");
            }

        } catch (MenuExitException e) {
            AppDetailsHelper.displayEndMessage();
            return;
        }





        /* MidiDevice.Info outputDevice = Arrays.stream(MidiSystem.getMidiDeviceInfo()).toList().get(2);
        MidiDevice virtualOutPort = MidiSystem.getMidiDevice(outputDevice); //out

        virtualOutPort.open();

        Receiver receiver = virtualOutPort.getReceiver();










        Configurator configurator = Configurator.loadFromFile("sounds/config.json");

        //Composition composition = new Composition("sounds/bwv525");
        //composition.print();

        //System.out.println("Playing " + configurator.getPieceName());

        /*
        KeyboardPool pool = new KeyboardPool(new File("sounds/bwv525"));
        //pool.setTempo(KeyboardPool.FASTER);

        pool.getKeyboards().getFirst().makeActive();
        pool.getKeyboards().get(1).makeActive();
        pool.getKeyboards().get(2).makeActive();



         */
        //OrganSequencer sequencer = new OrganSequencer(composition, receiver);
        /*
        sequencer.start();

        InputTest test = new InputTest(composition, receiver);
        test.start();

*/



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