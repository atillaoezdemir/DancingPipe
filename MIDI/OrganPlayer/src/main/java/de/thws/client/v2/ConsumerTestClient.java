package de.thws.client.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.OrganSequencer;

import javax.sound.midi.InvalidMidiDataException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class ConsumerTestClient extends Thread {
    private static final String SERVER_URI = "http://10.10.35.129:8080";
    private static final String SERVER_ENDPOINT = "/consumer";
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    OrganSequencer sequencer;

    public ConsumerTestClient(OrganSequencer sequencer) {
        this.sequencer = sequencer;
    }
    public void run() {
        HttpClient client = HttpClient.newHttpClient();

        //KeyboardPool pool = new KeyboardPool(new File("sounds/new"));
        //pool.getKeyboards().getFirst().makeActive();
        //OrganSequencer sequencer = new OrganSequencer(pool);

        listenToServer(client, sequencer);
    }

    private void listenToServer(HttpClient client, OrganSequencer sequencer) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + SERVER_ENDPOINT))
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(httpResponse -> httpResponse.body().forEach(b -> processLine(b, sequencer)))
                .join();
    }

    private void processLine(String line, OrganSequencer sequencer) {
        if (line.startsWith("data:")) {
            String json = line.substring(5).trim();
            parseAndHandleCommand(json, sequencer);
        }
    }

    private void parseAndHandleCommand(String json, OrganSequencer sequencer) {
        try {
            ConsumerDataInDTO commandData = objectMapper.readValue(json, ConsumerDataInDTO.class);
            handleCommand(commandData.getCommand(), sequencer);
            System.out.println("Received:\n command: " + commandData.getCommand() +
                    "\n Current tempo: " + commandData.getCurrentTempo() +
                    "\nKeyboards in use: " + commandData.getKeyboardsInUse());

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private void handleCommand(String command, OrganSequencer sequencer) {
        switch (command) {
            case "start":
                try {
                    sequencer.start();
                } catch (Exception e) {
                    System.err.println("Error starting sequencer: " + e.getMessage());
                }
                //todo add organ sequencer logic.
                sendConfiguration(sequencer.getNumberOfKeyboards(), sequencer.getNumberOfKeyboards());
                break;
            case "stop":
                try {
                    sequencer.stopPlaying();
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Received stop command. Waiting for next start.");
                sendConfiguration(0, 0);
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
                sequencer.setTempoDoDefault();
                break;

            default:
                //todo add organ sequencer logic.
                System.out.println("Unhandled command: " + command);
                break;
        }
    }

    private static void sendConfiguration(int keyboardsMax, int keyboardsInUse) {
        try {
            ConsumerDataOutDTO config = new ConsumerDataOutDTO(keyboardsMax, keyboardsInUse);
            String jsonPayload = objectMapper.writeValueAsString(config);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URI + SERVER_ENDPOINT))
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
            e.printStackTrace();
        }
    }

}
