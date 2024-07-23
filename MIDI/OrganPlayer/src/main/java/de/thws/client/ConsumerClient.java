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
 *
 */
public class ConsumerClient extends Thread {
    //private static String serverURI = "http://localhost:8080";
    private static String serverURI = "http://10.10.35.129:8080";
    private static final String SERVER_ENDPOINT = "/consumer";
    private static final ObjectMapper objectMapper = new ObjectMapper(); // deserializes JSON
    private String pathToComposition;
    Receiver receiver;
    OrganSequencer sequencer;

    public ConsumerClient(Receiver receiver, String pathToComposition, String serverURI) {
        this.pathToComposition = pathToComposition;
        this.receiver = receiver;
        ConsumerClient.serverURI = serverURI;
    }

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

                sendConfiguration(3, 3, lengthInBars, title, composer);

                break;
            case "stop":
                try {
                    sequencer.stopPlaying();
                    sequencer.join();
                } catch (InvalidMidiDataException | InterruptedException e) {
                    System.err.println("Error when stopping the sequencer: " + e.getMessage());
                    sendConfiguration(0, 0, lengthInBars, title, composer); // todo from Kirill
                    return;
                }
                System.out.println("Received stop command. Waiting for next start.");
                sendConfiguration(0, 0,lengthInBars, title, composer); // todo from Kirill
                break;
            case "incrementKeyboards":
                sequencer.incrementKeyboards();
                //todo add organ sequencer logic.
                break;
            case "decrementKeyboards":
                sequencer.decrementKeyboards();
                //todo add organ sequencer logic.
                break;
            case "maxKeyboards":
                sequencer.setKeyboardsToMax();
                //todo add organ sequencer logic.
                break;
            case "minKeyboards":
                sequencer.setKeyboardsToMin();
                //todo add organ sequencer logic.
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
                //todo add organ sequencer logic.
                System.out.println("Unhandled command: " + command);
                break;
        }
    }

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
