package de.thws.midi;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.io.FileInputStream;

public class MidiPlayer {
    private static Sequencer sequencer;

    static {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playMidiFile(String filepath) {
        try {
//            if (sequencer.isRunning()) {
//                sequencer.stop();
//            }
            sequencer.setSequence(new FileInputStream(new File(filepath)));
            sequencer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}