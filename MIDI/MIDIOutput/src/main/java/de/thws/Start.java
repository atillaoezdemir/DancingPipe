package de.thws;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Scanner;

import com.google.gson.Gson;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class Start {
    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        final MidiDevice.Info outputDeviceInfo = DevicePicker.chooseDevice();
        MidiDevice outputDevice = null;
        try {
            outputDevice = MidiSystem.getMidiDevice(outputDeviceInfo);
        } catch (MidiUnavailableException e) {
            System.out.println("Device " + outputDeviceInfo.getName() + " not available. This device could be currently in use by another application:");
        }
        if (outputDevice != null) {


            try {
                OrganSequencer sequencer = new OrganSequencer("sounds", outputDevice);
                RandomSequenceGenerator.runRandomTestSequence(sequencer);
            }
            catch (OrganSequencerException e) {
                System.out.println(e.getMessage());
                System.err.println("ERROR. Please restart the program.");
                System.exit(1);
            }
        }

        sc.close();

    }
}