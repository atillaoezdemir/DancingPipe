package de.thws;

import java.io.IOException;
import java.util.Scanner;

public class InputTest extends Thread {
    KeyboardPool pool;
    OrganSequencer sequencer;

    public InputTest(KeyboardPool pool, OrganSequencer sequencer) {
        this.pool = pool;
        this.sequencer = sequencer;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (sequencer.isPlaying) {
            try {
                if (System.in.available() > 0) {
                    String stringInput = sc.nextLine();
                    if (stringInput.equals("+")) {
                        System.out.println("Entered +");
                        sequencer.incrementKeyboards();
                        //pool.keyboards.get(1).makeActive();
                        //equencer.increaseTempo();
                    }
                    if (stringInput.equals("++")) {
                        System.out.println("Entered ++");
                        pool.keyboards.get(2).makeActive();
                    }
                    if (stringInput.equals("-")) {
                        System.out.println("Entered -");
                        //pool.keyboards.get(1).makeInactive();
                        //pool.setTempoFactor(KeyboardPool.SLOWER);
                        //sequencer.decreaseTempo();
                        sequencer.decrementKeyboards();
                    }
                    if (stringInput.equals("--")) {
                        System.out.println("Entered --");
                        pool.keyboards.get(2).makeInactive();
                    }

                    // todo when making inactive all notes off or keep active until next noteoff event
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
