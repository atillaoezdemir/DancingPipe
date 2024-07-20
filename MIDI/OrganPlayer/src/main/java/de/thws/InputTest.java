package de.thws;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import java.io.IOException;
import java.util.Scanner;

public class InputTest extends Thread {
    Composition composition;
    Receiver receiver;
    //OrganSequencer sequencer;

    public InputTest(Composition composition, Receiver receiver) {
        this.composition = composition;
        this.receiver = receiver;
        //this.sequencer = sequencer;
    }

    @Override
    public void run() {
        try {
            getInput();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void getInput() throws InterruptedException {
        OrganSequencer sequencer = null;
        Scanner sc = new Scanner(System.in);
        boolean done = false;
        //sequencer.startPlaying();
        while (!done) {
            /* if (!sequencer.isPlaying)
                done = true;

             */
            try {
                if (System.in.available() > 0) {
                    String stringInput = sc.nextLine();
                    if (stringInput.equals("start")) {
                        System.out.println("Entered start");
                        sequencer = new OrganSequencer(composition, receiver);
                        sequencer.start();
                        continue;
                    }
                    if (stringInput.equals("+")) {
                        System.out.println("Entered +");
                        sequencer.incrementKeyboards();
                        continue;
                    }
                    if (stringInput.equals("f")) {
                        System.out.println("Entered f");
                        sequencer.increaseTempo();
                        continue;
                    }
                    if (stringInput.equals("-")) {
                        System.out.println("Entered -");
                        sequencer.decrementKeyboards();
                        continue;
                    }
                    if (stringInput.equals("s")) {
                        System.out.println("Entered s");
                       sequencer.decreaseTempo();
                       continue;
                    }
                    if (stringInput.equals("stop")) {
                        System.out.println("Entered stop");
                        sequencer.stopPlaying();
                        sequencer.join();
                        System.out.println(sequencer.getState().toString());
                        if(sequencer.isAlive()) {
                            System.out.println("Thread is still alive");
                        }
                        continue;
                    }
                    if (stringInput.equals("exit")) {
                        System.out.println("Entered exit\nBye!");
                        done = true;
                    }
                    // todo when making inactive all notes off or keep active until next noteoff event
                }
            } catch (IOException | InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
        }

       // sequencer.interrupt();
    }
}
