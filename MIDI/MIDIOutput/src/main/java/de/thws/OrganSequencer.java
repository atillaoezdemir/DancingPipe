package de.thws;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.probeContentType;

public class OrganSequencer {
    private final File directoryPath; // path to folder with midi files
    private final Map<String, Sequence> midiFilesMap = new HashMap<>();
    private MidiDevice outPort;
    private Sequencer sequencer;
    private Transmitter transmitter;


    /**
     * @param path    Path to folder with midi Sounds
     * @param outPort Output MIDI Device for the output MIDI signals
     */
    public OrganSequencer(String path, MidiDevice outPort) throws OrganSequencerException {
        this.directoryPath = new File(path);
        addMidiFilesToSequenceMap();

        this.outPort = outPort;
        try {
            this.sequencer = MidiSystem.getSequencer(false);
            this.transmitter = sequencer.getTransmitter();
        } catch (MidiUnavailableException e) {
            System.out.println("Sequencer not available");
            this.sequencer = null;
            this.transmitter = null;

        }

        if (outPort != null && this.sequencer != null) {
            try {
                this.transmitter.setReceiver(outPort.getReceiver());
            } catch (MidiUnavailableException e) {
                throw new OrganSequencerException("MIDI Output Port not available. This device could be currently in use by another application.");
            }
        }
    }

    public boolean open() throws OrganSequencerException {
        if (sequencer != null && outPort != null) {
            try {
                if (!sequencer.isOpen()) {
                    sequencer.open();
                }
                if (!outPort.isOpen()) {
                    outPort.open();
                }
            } catch (MidiUnavailableException e) {
                throw new OrganSequencerException("Sequencer or MIDI Port not available. Please make sure that the MIDI Port is not in use by another application and try again.");
            }
            return true;
        }
        return false;
    }

    public boolean stopPlaying() {
        if (sequencer != null && outPort != null) {
            sequencer.stop();
            sequencer.close();
            outPort.close();
            return true;
        }
        return false;
    }

    /**
     * Changes the Sequence that is currently being played
     *
     * @param path Path to file which should start playing
     * @return
     */
    public boolean changeSequence(String path) {
        if (sequencer != null && outPort != null) {
            Sequence newSequence = midiFilesMap.get(path);
            if (newSequence == null) {
                System.out.println("Input Sequence not found.");
                return false;
            }
            if (sequencer.isRunning()) {
                if (sequencer.getSequence().equals(newSequence)) {
                    return false;
                }
                sequencer.stop();
            }

            try {
                sequencer.setSequence(midiFilesMap.get(path));
            } catch (InvalidMidiDataException e) {
                System.out.println("Invalid MIDI file. Please make sure that the MIDI files are not damaged.");
                sequencer.start();
                return false;
            }
            if(!sequencer.isRunning()) {
                sequencer.start();
                return true;
            }
        }
        return false;
    }

    public boolean isRunning() {
        return sequencer.isRunning();
    }

    public boolean isOpen() {
        return sequencer.isOpen();
    }

    public void setTempoInBPM(float bpm) {
        sequencer.setTempoInBPM(bpm);
    }

    public void setLoopCount(int loopCount) {
        sequencer.setLoopCount(loopCount);
    }


    private void addMidiFilesToSequenceMap() throws OrganSequencerException {
        final File[] filesInDirectory = directoryPath.listFiles();
        boolean directoryContainsMidiFiles = false;
        if (filesInDirectory != null) {
            for (File file : filesInDirectory) {
                if (isMidiFile(file)) {
                    directoryContainsMidiFiles = true;
                    try {
                        Sequence fileAsSequence = MidiSystem.getSequence(file);
                        midiFilesMap.put(file.getName(), fileAsSequence);
                    } catch (IOException e) {
                        throw new OrganSequencerException("IOException thrown when reading the MIDI files.\nPlease make sure that the folder with the MIDI files is not empty and the MIDI files are not damaged.");
                    } catch (InvalidMidiDataException e) {
                        throw new OrganSequencerException("InvalidMidiDataException thrown when reading the MIDI files.\nPlease make sure that the MIDI files are not damaged.");
                    }
                }
            }

        } else {
            throw new OrganSequencerException("No files in directory " + directoryPath.getAbsolutePath());

        }
        if (!directoryContainsMidiFiles) {
            throw new OrganSequencerException("No MIDI files in directory " + directoryPath.getAbsolutePath());
        }
    }


    private boolean isMidiFile(File file) {
        String contentType;
        try {
            contentType = probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (contentType == null) {
            return false;
        }
        return contentType.equals("audio/mid");
    }


}
