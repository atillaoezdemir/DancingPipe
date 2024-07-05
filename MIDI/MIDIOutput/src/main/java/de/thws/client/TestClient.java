package de.thws.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class TestClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int DELAY_MIN_VALUE = 500;
    private static final int DELAY_MAX_VALUE = 1500;
    private static final int NUMBER_CAP = 255;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Random random = new Random();
            while (true) {
                int number = random.nextInt(NUMBER_CAP) + 1;
                out.println(number);
                System.out.println("Sent: " + number);

                int delay = DELAY_MIN_VALUE + random.nextInt(DELAY_MAX_VALUE-DELAY_MIN_VALUE);
                //int delay = 1500;
                Thread.sleep(delay);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}