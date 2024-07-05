package de.thws;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class DevicePicker {

    public static MidiDevice.Info chooseDevice() {
        final MidiDevice.Info[] midiDevicesInfo = MidiSystem.getMidiDeviceInfo(); // list of available MIDI devices
        List<MidiDevice.Info> outputMidiDevicesInfo = new ArrayList<MidiDevice.Info>();

        int deviceIndex = 0;
        System.out.println("Available Output MIDI devices:");
        for(MidiDevice.Info deviceInfo : midiDevicesInfo) {
            try {
                if(MidiSystem.getMidiDevice(deviceInfo).getClass().getTypeName().equals("com.sun.media.sound.MidiOutDevice")) {
                    outputMidiDevicesInfo.add(deviceInfo);
                    System.out.println("[" + deviceIndex + "] " + deviceInfo.getName() + " - " + deviceInfo.getDescription());
                    deviceIndex++;
                }
            }
           catch(MidiUnavailableException e) {
                System.out.println("Device " + deviceInfo.getName() + " not available. This device could be currently in use by another application:");
          }
        }
        if(outputMidiDevicesInfo.isEmpty()) {
            System.out.println("No output MIDI devices available. Please try again by restarting the application.");
            return null;
        }
        Scanner sc = new Scanner(System.in);
        MidiDevice.Info outputDevice = null;
        int input = Integer.MAX_VALUE;
        boolean isInputValid = false;

        while(!isInputValid) {
            System.out.print("Enter MIDI Device number: ");
            try {
                input = sc.nextInt();
            }
            catch(InputMismatchException e) {
                System.out.println("Invalid MIDI Device number! Please try again.");
                sc.nextLine();
                continue;
            }

            if(input >= outputMidiDevicesInfo.size() || input < 0) {
                System.out.println("ERROR: MIDI Device number our of bonds. Please try again.");
            }
            else {
                isInputValid = true;
                outputDevice = outputMidiDevicesInfo.get(input);
            }
        }
        return outputDevice;
    }

    public static void main(String[] args) {
        /*
        final MidiDevice.Info[] midiDevicesInfo = MidiSystem.getMidiDeviceInfo(); // list of available MIDI devices
        MidiDevice.Info device = midiDevicesInfo[4];
        MidiDevice outputDevice = MidiSystem.getMidiDevice(device);

         */

    }

}
