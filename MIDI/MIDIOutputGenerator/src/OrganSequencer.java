import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.probeContentType;

public class OrganSequencer {
    private final File directoryPath; // path to folder with midi files
    private final List<Sequence> midiFiles;
    private MidiDevice outPort;
    private Sequencer sequencer;



    public OrganSequencer(String path, MidiDevice outPort) {
        this.directoryPath = new File(path);
        midiFiles = new ArrayList<Sequence>();
        try {
            addMidiFilesToSequenceArray();

        } catch (IOException e) {
            System.out.println("IOException thrown when reading the MIDI files.\nPlease make sure that the folder with the MIDI files is not empty and the MIDI files are not damaged.");
        } catch (InvalidMidiDataException e) {
            System.out.println("InvalidMidiDataException thrown when reading the MIDI files.\nPlease make sure that the MIDI files are not damaged.");
        }
        this.outPort = outPort;
    }

    public boolean isRunning() {
        return sequencer.isRunning();
    }

    public void setTempoInBPM(float bpm) {
        sequencer.setTempoInBPM(bpm);
    }



    private void addMidiFilesToSequenceArray() throws InvalidMidiDataException, IOException {
        final File[] filesInDirectory = directoryPath.listFiles();
        boolean directoryContainsMidiFiles = false;
        if (filesInDirectory != null) {
            for (File file : filesInDirectory) {
                if (isMidiFile(file)) {
                    directoryContainsMidiFiles = true;
                    Sequence fileAsSequence = MidiSystem.getSequence(file);
                    midiFiles.add(fileAsSequence);
                }
            }
        } else {
            System.out.println("No files in directory " + directoryPath.getAbsolutePath());
            return;
        }
        if (!directoryContainsMidiFiles) {
            System.out.println("No MIDI files in directory " + directoryPath.getAbsolutePath());
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