package de.thws;

import com.diogonunes.jcolor.Attribute;
import de.thws.components.Composition;
import de.thws.exceptions.ConfiguratorException;
import de.thws.exceptions.OrganSequencerException;
import de.thws.helpers.AppDetailsHelper;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import java.io.IOException;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class InputTest extends Thread {
    private static final String START_INPUT = "start";
    private static final String STOP_INPUT = "stop";
    private static final String ADD_KEYBOARD_INPUT = "+";
    private static final String REMOVE_KEYBOARD_INPUT = "-";
    private static final String MAX_KEYBOARDS_INPUT = "++";
    private static final String MIN_KEYBOARDS_INPUT = "--";
    private static final String INCREMENT_TEMPO_INPUT = "f";
    private static final String DECREMENT_TEMPO_INPUT = "s";
    private static final String DEFAULT_TEMPO_INPUT = "d";


    private final String pathToComposition;
    Receiver receiver;
    OrganSequencer sequencer;

    public InputTest(Receiver receiver, String pathToComposition) {
        this.pathToComposition = pathToComposition;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        displayCommandInformation();
        printEnterCommandPrompt();
        getInput();
    }

    /**
     * Reads the input from the user and processes it.
     */
    private void getInput() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                if (System.in.available() > 0) {
                    String stringInput = sc.nextLine();
                    switch (stringInput.toLowerCase()) {
                        case START_INPUT:
                            if(sequencer != null && sequencer.isAlive()) {
                                printIllegalInputPrompt(false);
                                break;
                            }
                            System.out.println("Starting sequencer...");
                            Composition composition = null;
                            try {
                                composition = new Composition(this.pathToComposition);
                            } catch (ConfiguratorException | OrganSequencerException e) {
                                AppDetailsHelper.displayErrorMessage("Could not start the sequencer: " + e.getMessage());
                                return;
                            }
                            this.sequencer = new OrganSequencer(composition, this.receiver);
                            sequencer.start();
                            if (sequencer.isAlive()) {
                                System.out.println(colorize("Sequencer started!", Attribute.BLUE_BACK(), Attribute.BLACK_TEXT()));
                            }
                            break;

                        case STOP_INPUT, ADD_KEYBOARD_INPUT, REMOVE_KEYBOARD_INPUT, MAX_KEYBOARDS_INPUT, MIN_KEYBOARDS_INPUT, INCREMENT_TEMPO_INPUT, DECREMENT_TEMPO_INPUT, DEFAULT_TEMPO_INPUT, "exit":
                            handleInput(stringInput.toLowerCase());
                            if(stringInput.equalsIgnoreCase("exit")) {
                                return;
                            }
                            break;

                        default:
                            System.out.println("Invalid input");
                    }
                    printEnterCommandPrompt();
                }
            } catch (IOException e) {
                AppDetailsHelper.displayErrorMessage("Error when reading the commands: " + e.getMessage());
            }
        }
    }

    /**
     * Displays information about the commands.
     */
    private static void displayCommandInformation() {
        String delim = "->";
        printDisplayInformationMsg("Use the following commands to control the sequencer:\n");
        printDisplayInformationMsgInBold("\t" + START_INPUT);
        printDisplayInformationMsg(" " + delim + " start the sequencer\n");

        printDisplayInformationMsgInBold("\t" + STOP_INPUT);
        printDisplayInformationMsg(" " + delim + " stop the sequencer\n");

        printDisplayInformationMsgInBold("\t" + ADD_KEYBOARD_INPUT);
        printDisplayInformationMsg(" " + delim + " add keyboard\n");

        printDisplayInformationMsgInBold("\t" + REMOVE_KEYBOARD_INPUT);
        printDisplayInformationMsg(" " + delim + " remove keyboard\n");

        printDisplayInformationMsgInBold("\t" + MAX_KEYBOARDS_INPUT);
        printDisplayInformationMsg(" " + delim + " set keyboards to maximum\n");

        printDisplayInformationMsgInBold("\t" + MIN_KEYBOARDS_INPUT);
        printDisplayInformationMsg(" " + delim + " set keyboards to minimum\n");

        printDisplayInformationMsgInBold("\t" + INCREMENT_TEMPO_INPUT);
        printDisplayInformationMsg(" " + delim + " increment tempo\n");

        printDisplayInformationMsgInBold("\t" + DECREMENT_TEMPO_INPUT);
        printDisplayInformationMsg(" " + delim + " decrement tempo\n");

        printDisplayInformationMsgInBold("\t" + DEFAULT_TEMPO_INPUT);
        printDisplayInformationMsg(" " + delim + " set tempo to default\n");
    }

    private static void printDisplayInformationMsg(String msg) {
        System.out.print(colorize(msg, Attribute.YELLOW_TEXT()));
    }

    private static void printDisplayInformationMsgInBold(String msg) {
        System.out.print(colorize(msg, Attribute.YELLOW_TEXT(), Attribute.BOLD()));
    }

    private static void printEnterCommandPrompt() {
        printDisplayInformationMsg("Enter command: ");
    }

    /**
     * Handles the {@code input}. If it is illegal (that means a command to
     * control the sequencer was entered before the sequencer was started)
     * an appropriate prompt informing about the problem is displayed.
     * Otherwise, the corresponding command is being processed.
     * @param input the input command
     */
    private void handleInput(String input) {
        if (illegalInputCondition()) {
            printIllegalInputPrompt(true);
        } else {
            switch (input) {
                case ADD_KEYBOARD_INPUT:
                    System.out.println("Add Keyboard");
                    sequencer.incrementKeyboards();
                    break;

                case REMOVE_KEYBOARD_INPUT:
                    System.out.println("Remove Keyboard");
                    sequencer.decrementKeyboards();
                    break;

                case MAX_KEYBOARDS_INPUT:
                    System.out.println("Maximum Keyboards");
                    sequencer.setKeyboardsToMax();
                    break;

                case MIN_KEYBOARDS_INPUT:
                    System.out.println("Minimum Keyboards");
                    sequencer.setKeyboardsToMin();
                    break;

                case INCREMENT_TEMPO_INPUT:
                    System.out.println("Increment Tempo");
                    sequencer.increaseTempo();
                    break;

                case DECREMENT_TEMPO_INPUT:
                    System.out.println("Decrement Tempo");
                    sequencer.decreaseTempo();
                    break;

                case DEFAULT_TEMPO_INPUT:
                    System.out.println("Default Tempo");
                    sequencer.setTempoToDefault();
                    break;

                case STOP_INPUT, "exit":
                    System.out.println("Stopping sequencer...");
                    try {
                        sequencer.stopPlaying();
                        sequencer.join();
                    } catch (InvalidMidiDataException | InterruptedException e) {
                        AppDetailsHelper.displayErrorMessage("Error when stopping the sequencer: " + e.getMessage());
                    }
                    if(!input.equals("exit")) {
                        System.out.println("Waiting for next start...");

                    }
                    break;
            }
        }


    }

    /**
     * Displays a message informing the user that a start / stop command should be entered first.
     * @param start {@code true} if the start command should have been entered first,
     * {@code false} if the stop command should have been entered first
     */
    private void printIllegalInputPrompt(boolean start) {
        String command = start ? "start" : "stop";
        if(!start) {
            printDisplayInformationMsgInBold("Sequencer is currently running. ");
        }
        printDisplayInformationMsgInBold("You should first enter a " + command + " command\n");

    }


    /**
     * Checks if the condition for illegal input (sequencer hasn't been started yet) is fulfilled
     * @return {@code true} if the condition is fulfilled, {@code false} otherwise
     */
    private boolean illegalInputCondition() {
        return sequencer == null || !sequencer.isAlive();
    }
}
