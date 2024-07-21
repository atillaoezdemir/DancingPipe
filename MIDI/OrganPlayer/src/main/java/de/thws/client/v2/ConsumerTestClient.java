package de.thws.client.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.Composition;
import de.thws.ConfiguratorException;
import de.thws.OrganSequencer;
import de.thws.OrganSequencerException;

import javax.sound.midi.InvalidMidiDataException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import javax.sound.midi.*;

public class ConsumerTestClient extends Thread {
    private static final String SERVER_URI = "http://10.10.35.129:8080";
    private static final String SERVER_ENDPOINT = "/consumer";
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String pathToComposition;
    Receiver receiver;
    OrganSequencer sequencer;

    public ConsumerTestClient(Receiver receiver, String pathToComposition) {
       // super(name);
        this.pathToComposition = pathToComposition;
        this.receiver = receiver;
    }

    public void run() {
        HttpClient client = HttpClient.newHttpClient();
        listenToServer(client);
        System.out.println("Starting client...");
    }

    private void listenToServer(HttpClient client) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + SERVER_ENDPOINT))
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

                //todo add organ sequencer logic.
                break;
            case "stop":
                try {
                    sequencer.stopPlaying();
                    sequencer.join();
                } catch (InvalidMidiDataException | InterruptedException e) {
                    System.err.println("Error when stopping the sequencer: " + e.getMessage());
                    sendConfiguration(0, 0,lengthInBars,title,composer); // todo from Kirill
                    return;
                }
                System.out.println("Received stop command. Waiting for next start.");
                sendConfiguration(0, 0,lengthInBars,title,composer); // todo from Kirill
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

    private static void sendConfiguration(int keyboardsMax, int keyboardsInUse,long barLength,String title,String composerName) {
        try {
            ConsumerDataOutDTO config = new ConsumerDataOutDTO(keyboardsMax, keyboardsInUse, barLength, title, composerName);
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
//                        System.out.println("Length in bars: " + response.body());
//                        System.out.println("Title: " + response.body());
//                        System.out.println("Composer name: " + response.body());
                    })
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
