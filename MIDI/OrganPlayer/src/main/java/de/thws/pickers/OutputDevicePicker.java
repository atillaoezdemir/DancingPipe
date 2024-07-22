package de.thws.pickers;

import com.diogonunes.jcolor.Attribute;
import de.thws.exceptions.MenuExitException;
import de.thws.helpers.AppDetailsHelper;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * Lets the user pick an output device, on which the MIDI signals will be sent. The available MIDI devices are being obtained using the {@link MidiSystem} class.
 */
public class OutputDevicePicker {
    /**
     * Lists the available MIDI output devices and lets the user pick one
     * @return the chosen device as {@link MidiDevice.Info} object
     * @throws MenuExitException if the user entered an exit command
     */
    public static MidiDevice.Info chooseDevice() throws MenuExitException {
        List<MidiDevice.Info> outputMidiDevicesInfo = getAndPrintMidiOutputDevices();
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
                AppDetailsHelper.checkIfExitEntered(sc);
                System.out.println(colorize("Invalid MIDI Device number! Please try again.", Attribute.BRIGHT_BLUE_BACK(), Attribute.BLACK_TEXT()));
                sc.nextLine();
                continue;
            }

            if(input >= outputMidiDevicesInfo.size() || input < 0) {
                AppDetailsHelper.displayErrorMessage("ERROR: MIDI Device number our of bonds. Please try again.");
            }
            else {
                isInputValid = true;
                outputDevice = outputMidiDevicesInfo.get(input);
            }
        }
        return outputDevice;
    }


    /**
     * Gets the available output MIDI devices at the moment and prints them.
     * @return list of the available MIDI output devices as {@link List} of {@link MidiDevice.Info} objects
     */
    private static List<MidiDevice.Info> getAndPrintMidiOutputDevices () {
        final MidiDevice.Info[] midiDevicesInfo = MidiSystem.getMidiDeviceInfo(); // list of available MIDI devices
        List<MidiDevice.Info> outputMidiDevicesInfo = new ArrayList<>();
        int deviceIndex = 0;
        System.out.println(colorize("Available Output MIDI devices:", Attribute.BOLD()));
        for(MidiDevice.Info deviceInfo : midiDevicesInfo) {
            try {
                if(MidiSystem.getMidiDevice(deviceInfo).getClass().getTypeName().equals("com.sun.media.sound.MidiOutDevice")) { // only list the output devices
                    outputMidiDevicesInfo.add(deviceInfo);
                    System.out.print(colorize("[" + deviceIndex + "] ", Attribute.BRIGHT_BLUE_TEXT()));
                    System.out.println(midiDeviceToString(deviceInfo));
                    deviceIndex++;
                }
            }
            catch(MidiUnavailableException e) {
                System.out.println(colorize("Device " + deviceInfo.getName() + " not available. This device could be currently in use by another application.", Attribute.BRIGHT_BLUE_BACK(), Attribute.BLACK_TEXT()));
            }
        }
        if(outputMidiDevicesInfo.isEmpty()) {
            System.out.println(colorize("No output MIDI devices available. Please try again by restarting the application.",Attribute.RED_BACK(), Attribute.BLACK_TEXT()));
        }
        return outputMidiDevicesInfo;
    }

    /**
     * @param device the device to be used
     * @return {@code device} name and description as {@link String}
     */
    private static String midiDeviceToString(MidiDevice.Info device) {
        return device.getName() + " - " + device.getDescription();
    }
}
