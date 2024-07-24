package de.thws.client;

import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.components.Composition;
import de.thws.exceptions.ConfiguratorException;
import de.thws.OrganSequencer;
import de.thws.exceptions.OrganSequencerException;

import javax.sound.midi.InvalidMidiDataException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.sound.midi.*;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * Client class that connects with the server and processes commands from it. This class extends
 * {@link Thread} to allow real-time control of the {@link Sequencer}.
 *
 * <p><strong>Class members</strong>:
 * <ul>
 *     <li>{@code serverURI} - the server address as {@link String}.
 *     <li>{@code SERVER_ENDPOINT} - endpoint for the server, default is {@code /consumer}.
 *     <li>{@code objectMapper} - {@link ObjectMapper} object used for serializing and deserializing the messages from the server.
 *     <li>{@code pathToComposition} - path to the composition that is about to be played as {@link String}.
 *     <li>{@code receiver} - MIDI device, on which the MIDI signals will be sent. The required type is {@link Receiver}.
 *     <li>{@code sequencer} - The {@link OrganSequencer} object used by the server.
 * </ul>
 *
 * <p>This class extends {@link Thread} to allow asynchronous handling of MIDI events and server commands. The thread functionality is
 * necessary to ensure that the {@link OrganSequencer} is controlled in real-time.
 *
 * @see Thread
 * @see Sequencer
 * @see Receiver
 * @see OrganSequencer
 */
public class ConsumerClient extends Thread {
    //private static String serverURI = "http://localhost:8080";
    private static String serverURI = "http://10.10.35.129:8080";
    private static final String SERVER_ENDPOINT = "/consumer";
    private static final ObjectMapper objectMapper = new ObjectMapper(); // deserializes JSON
    private final String pathToComposition;
    Receiver receiver;
    OrganSequencer sequencer;

    public ConsumerClient(Receiver receiver, String pathToComposition, String serverURI) {
        this.pathToComposition = pathToComposition;
        this.receiver = receiver;
        ConsumerClient.serverURI = serverURI;
    }

    /**
     * Starts the client.
     */
    public void run() {
        System.out.println(colorize("Connecting server on " + serverURI + " ...", Attribute.YELLOW_TEXT(), Attribute.BOLD()));

        HttpClient client = HttpClient.newHttpClient();
        listenToServer(client);
    }

    private void listenToServer(HttpClient client) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverURI + SERVER_ENDPOINT))
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(httpResponse -> httpResponse.body().forEach(this::processLine))
                .join();
    }

    private void processLine(String line) {

        if (line.startsWith("data:")) {
            String json = line.substring(5).trim();
            parseAndHandleCommand(json);
        }
    }

    /**
     * Handles commands from the server.
     * @param json message from server as {@link String}
     */
    private void parseAndHandleCommand(String json) {
        try {
            ConsumerDataInDTO commandData = objectMapper.readValue(json, ConsumerDataInDTO.class);
            handleCommand(commandData.getCommand());
            System.out.println("Received:\n command: " + commandData.getCommand() +
                    "\n Current tempo: " + commandData.getCurrentTempo() +
                    "\nKeyboards in use: " + commandData.getKeyboardsInUse());

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    /**
     * Handle commands received from the server and trigger the corresponding commands on the sequencer.
     * @param command command received from the server
     */
    private void handleCommand(String command) {
        String title = "";
        String composer = "";
        long lengthInBars = 0L;
        switch (command) {
            case "start":
                Composition composition = null;
                try {
                    composition = new Composition(this.pathToComposition);
                } catch (ConfiguratorException | OrganSequencerException e) {
                    System.err.println("Could not start the sequencer: " + e.getMessage());
                    return;
                }
                title = composition.getName();
                composer = composition.getComposer();
                lengthInBars = composition.getLengthInBars();
                this.sequencer = new OrganSequencer(composition, this.receiver);
                sequencer.start();

                sendConfiguration(composition.getKeyboardPool().getKeyboards().size(), 1, lengthInBars, title, composer);

                break;
            case "stop":
                try {
                    sequencer.stopPlaying();
                    sequencer.join();
                } catch (InvalidMidiDataException | InterruptedException e) {
                    System.err.println("Error when stopping the sequencer: " + e.getMessage());
                    sendConfiguration(0, 0, 0, "stopped", "stopped");
                    return;
                }
                System.out.println("Received stop command. Waiting for next start.");
                sendConfiguration(0, 0, 0, "stopped", "stopped");
                break;
            case "incrementKeyboards":
                sequencer.incrementKeyboards();
                break;
            case "decrementKeyboards":
                sequencer.decrementKeyboards();
                break;
            case "maxKeyboards":
                sequencer.setKeyboardsToMax();
                break;
            case "minKeyboards":
                sequencer.setKeyboardsToMin();
                break;
            case "incrementTempo":
                sequencer.increaseTempo();
                break;
            case "decrementTempo":
                sequencer.decreaseTempo();
                break;
            case "defaultTempo":
                sequencer.setTempoToDefault();
                break;

            default:
                System.out.println("Unhandled command: " + command);
                break;
        }
    }

    /**
     * Sends configuration message to the server.
     * This message is used in the frontend to display information about the composition to the user.
     * @param keyboardsMax the maximal number of keyboards used in the composition as {@code int}
     * @param keyboardsInUse the initial number of keyboards that are active when starting the sequencer as {@code int}. (Normally {@code 1})
     * @param barLength the length of the composition in bars as {@code long}
     * @param title title of the composition as {@link String}
     * @param composerName composer's name as {@link String}
     */
    private static void sendConfiguration(int keyboardsMax, int keyboardsInUse,long barLength,String title,String composerName) {
        try {
            ConsumerDataOutDTO config = new ConsumerDataOutDTO(keyboardsMax, keyboardsInUse, barLength, title, composerName);
            String jsonPayload = objectMapper.writeValueAsString(config);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverURI + SERVER_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("Configuration status: " + response.statusCode());
                        System.out.println("Response body: " + response.body());
                    })
                    .join();
        } catch (Exception e) {
            System.err.println("Error when sending message to the server: " + e.getMessage());
        }
    }

}
