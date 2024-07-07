package de.thws.client.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.KeyboardPool;
import de.thws.OrganSequencer;
import de.thws.OrganSequencerException;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class ConsumerTestClient {
    private static final String SERVER_URI = "http://localhost:8080";
    private static final String SERVER_ENDPOINT = "/consumer";
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws OrganSequencerException {
        HttpClient client = HttpClient.newHttpClient();

        KeyboardPool pool = new KeyboardPool(new File("sounds/new"));
        pool.getKeyboards().getFirst().makeActive();
        OrganSequencer sequencer = new OrganSequencer(pool);

        listenToServer(client, sequencer);
    }

    private static void listenToServer(HttpClient client,  OrganSequencer sequencer) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + SERVER_ENDPOINT))
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(httpResponse -> httpResponse.body().forEach(b -> processLine(b, sequencer)))
                .join();
    }

    private static void processLine(String line,OrganSequencer sequencer) {
        if (line.startsWith("data:")) {
            String json = line.substring(5).trim();
            parseAndHandleCommand(json, sequencer);
        }
    }

    private static void parseAndHandleCommand(String json, OrganSequencer sequencer) {
        try {
            ConsumerDataInDTO commandData = objectMapper.readValue(json, ConsumerDataInDTO.class);
            handleCommand(commandData.getCommand(), sequencer);
            System.out.println("Received:\n command: " + commandData.getCommand() +
                    "\n Current tempo: "+ commandData.getCurrentTempo()+
                    "\nKeyboards in use: " + commandData.getKeyboardsInUse());

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private static void handleCommand(String command, OrganSequencer sequencer) {
        switch (command) {
            case "start":
                try {
                    sequencer.start();
                }
                catch (Exception e) {
                    System.err.println("Error starting sequencer: " + e.getMessage());
                }
                //todo add organ sequencer logic.
                sendConfiguration(random.nextInt(2) + 3, random.nextInt(2) + 1);
                break;
            case "stop":
                //todo add organ sequencer logic.
                System.out.println("Received stop command. Waiting for next start.");
                sendConfiguration(0, 0);
                break;
            case "incrementKeyboards":
                //todo add organ sequencer logic.
                break;
            case "decrementKeyboards":
                //todo add organ sequencer logic.
                break;
            case "maxKeyboards":
                //todo add organ sequencer logic.
                break;
            case "minKeyboards":
                //todo add organ sequencer logic.
                break;
            case "incrementTempo":
                //todo add organ sequencer logic.
                break;
                case "decrementTempo":
                //todo add organ sequencer logic.
                break;
                case "defaultTempo":
                //todo add organ sequencer logic.
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
