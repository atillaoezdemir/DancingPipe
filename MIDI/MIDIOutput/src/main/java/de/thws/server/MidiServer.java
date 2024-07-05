package de.thws.server;

import de.thws.OrganSequencer;
import de.thws.OrganSequencerException;
import de.thws.RandomSequenceGenerator;
import de.thws.midi.MidiPlayer;
import de.thws.mapper.FileMapper;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                                MidiDevice outputDevice = null;
                                try {
                                    final MidiDevice.Info[] midiDevicesInfo = MidiSystem.getMidiDeviceInfo(); // list of available MIDI devices
                                    MidiDevice.Info device = midiDevicesInfo[2];
                                    outputDevice = MidiSystem.getMidiDevice(device);
                                }

                                catch (MidiUnavailableException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    OrganSequencer sequencer = new OrganSequencer("sounds", outputDevice);
                                    sequencer.open();
                                    //String filename = filepath.substring(7, filepath.length());

                                    sequencer.changeSequence(filepath);
                                    //
                                }
                                catch (OrganSequencerException e) {
                                    System.out.println(e.getMessage());
                                    System.err.println("ERROR. Please restart the program.");
                                    System.exit(1);
                                }

                                //MidiPlayer.playMidiFile(filepath);
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