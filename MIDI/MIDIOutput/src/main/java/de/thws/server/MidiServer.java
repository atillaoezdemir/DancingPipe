package de.thws.server;

import de.thws.midi.MidiPlayer;
import de.thws.mapper.FileMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MidiServer {
    private static final Logger LOGGER = Logger.getLogger(MidiServer.class.getName());
    private static Map<Integer, String> midiMap = new HashMap<>();

    public static void startServer() {
        try {

            FileMapper[] fileMappers = FileMapper.loadMappers("mapper.json");


            for (FileMapper fm : fileMappers) {
                for (int i = (int) fm.lowerBorder; i <= fm.upperBorder; i++) {
                    midiMap.put(i, fm.filepath);
                }
            }

            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                LOGGER.info("Server is listening on port 12345");

                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                        LOGGER.info("Client connected: " + clientSocket.getInetAddress());

                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            LOGGER.info("Received: " + inputLine);
                            int digit = Integer.parseInt(inputLine);
                            String filepath = midiMap.get(digit);
                            if (filepath != null) {
                                MidiPlayer.playMidiFile(filepath);
                            } else {
                                LOGGER.warning("No MIDI file mapped for digit: " + digit);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startServer();
    }
}