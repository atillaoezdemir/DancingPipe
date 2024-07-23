package de.thws;

import de.thws.client.ConsumerClient;
import de.thws.exceptions.MenuExitException;
import de.thws.helpers.AppDetailsHelper;
import de.thws.pickers.CompositionPicker;
import de.thws.pickers.ModePicker;
import de.thws.pickers.OutputDevicePicker;
import de.thws.pickers.ServerAddressPicker;

import javax.sound.midi.*;

/**
 * Starting point for the application. Use this class to start the application.
 */
public class Start {
    public static void main(String[] args) {
        AppDetails.displayDetails();

        try {
            final MidiDevice.Info outputDeviceInfo = OutputDevicePicker.chooseDevice();
            MidiDevice outputDevice = null;
            try {
                outputDevice = MidiSystem.getMidiDevice(outputDeviceInfo);
            } catch (MidiUnavailableException e) {
                AppDetailsHelper.displayErrorMessage("Device " + outputDeviceInfo.getName() + " not available. This device could be currently in use by another application:");
                throw new MenuExitException("");
            }

            if (outputDevice != null) {
                Receiver receiver;
                try  {
                    outputDevice.open();
                    receiver = outputDevice.getReceiver();
                }
                catch (MidiUnavailableException e) {
                    AppDetailsHelper.displayErrorMessage("Error when opening " + outputDeviceInfo.getName() + ". This device could be currently in use by another application:");
                    throw new MenuExitException("");
                }


                CompositionPicker cp = new CompositionPicker("sounds");

                String compositionPath = cp.pickComposition();
                if (!compositionPath.isEmpty()) {
                    boolean mode = ModePicker.pickMode();
                    try {
                        if (mode) {
                            String serverURI = ServerAddressPicker.pickServerAddress();
                            if(serverURI.isEmpty()) {
                                throw new MenuExitException("");
                            }

                            ConsumerClient client = new ConsumerClient(receiver, compositionPath, serverURI);
                            client.start();
                            client.join();
                        } else {
                            UserInput userInput = new UserInput(receiver, compositionPath);
                            userInput.start();
                            userInput.join();
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
        }
    }
}