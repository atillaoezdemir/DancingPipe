package com.example.testClients.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class ConsumerTestClient {
    private static final String SERVER_URI = "http://localhost:8080";
    private static final String CONFIG_ENDPOINT = "/special/config";
    private static final String SPECIAL_ENDPOINT = "/special";
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        listenToServer(client);
    }

    private static void listenToServer(HttpClient client) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + SPECIAL_ENDPOINT))
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(httpResponse -> httpResponse.body().forEach(ConsumerTestClient::processLine))
                .join(); // Wait for all operations to complete
    }

    private static void processLine(String line) {
        if (line.startsWith("data:")) {
            String json = line.substring(5).trim();
            parseAndHandleCommand(json);
        }
    }

    private static void parseAndHandleCommand(String json) {
        try {
            ConsumerDataInDTO commandData = objectMapper.readValue(json, ConsumerDataInDTO.class);
            handleCommand(commandData.getCommand());
            System.out.println("Received command: " + commandData.getCommand() +
                    "\nReceived: Keyboards in use = " + commandData.getKeyboardsInUse());

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private static void handleCommand(String command) {
        switch (command) {
            case "start":
                //todo add organ sequencer logic.
                sendConfiguration(random.nextInt(2) + 3, random.nextInt(2) + 1); // Randomly 3 or 4 max keyboards
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
                    .uri(URI.create(SERVER_URI + CONFIG_ENDPOINT))
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
