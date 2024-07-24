package com.example.testClients.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
//used only it test purposes.
public class ConsumerTestClient {
    private static final String SERVER_URI = "http://localhost:8080";
    private static final String SERVER_ENDPOINT = "/consumer";
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        listenToServer(client);
    }

    private static void listenToServer(HttpClient client) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URI + SERVER_ENDPOINT))
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(httpResponse -> httpResponse.body().forEach(ConsumerTestClient::processLine))
                .join();
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
            System.out.println("Received:\n command: " + commandData.getCommand() +
                    "\n Current tempo: " + commandData.getCurrentTempo() +
                    "\nKeyboards in use: " + commandData.getKeyboardsInUse());

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    private static void handleCommand(String command) {
        switch (command) {
            case "start":
                sendConfiguration(random.nextInt(2) + 3, random.nextInt(2) + 1, 100, "Test", "Test");
                break;
            case "stop":
                System.out.println("Received stop command. Waiting for next start.");
                sendConfiguration(0, 0, 0, "stopped", "stopped");
                break;
            case "incrementKeyboards":
                System.out.println("incrementKeyboards");
                break;
            case "decrementKeyboards":
                System.out.println("decrementKeyboards");
                break;
            case "maxKeyboards":
                System.out.println("maxKeyboards");
                break;
            case "minKeyboards":
                System.out.println("minKeyboards");
                break;
            case "incrementTempo":
                System.out.println("incrementTempo");
                break;
            case "decrementTempo":
                System.out.println("decrementTempo");
                break;
            case "defaultTempo":
                System.out.println("defaultTempo");
                break;

            default:

                System.out.println("Unhandled command: " + command);
                break;
        }
    }

    private static void sendConfiguration(int keyboardsMax, int keyboardsInUse, int barLength, String title, String composerName) {
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
                    })
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
