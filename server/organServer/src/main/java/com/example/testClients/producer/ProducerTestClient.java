package com.example.testClients.producer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
//used only it test purposes.
public class ProducerTestClient {
    private static final String SERVER_URL = "http://localhost:8080/producer";
    private static final int DELAY_MIN_VALUE = 500;
    private static final int DELAY_MAX_VALUE = 500;
    private static final int NUMBER_CAP = 255;
    private static final String MODE = "sequence"; // "random" or "sequence"

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        if (MODE.equals("sequence")) {
            int[] sequence = {0,3,3,3,2,2,2,5,11,11,11,11,6,6,6,6,6,16,21,26};
            sendNumbers(client, sequence);
        } else {
            sendRandomNumbers(client);
        }
    }

    private static void sendNumbers(HttpClient client, int[] numbers) {
        for (int number : numbers) {
            sendNumber(client, number);
        }
    }

    private static void sendRandomNumbers(HttpClient client) {
        while (true) {
            int number = (int) (Math.random() * NUMBER_CAP) + 1;
            sendNumber(client, number);
        }
    }

    private static void sendNumber(HttpClient client, int number) {
        String json = String.format("{\"number\":%d}", number);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Sent: " + number + " - Response: " + response.body());

            int delay = DELAY_MIN_VALUE + (int) (Math.random() * (DELAY_MAX_VALUE - DELAY_MIN_VALUE));
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
