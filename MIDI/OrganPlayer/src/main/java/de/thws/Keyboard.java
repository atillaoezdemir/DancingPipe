package de.thws;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class Keyboard {
    private final List<Pattern> keyboardPatterns;
    private final int numberOfPatterns;
    private long lastTick;
    private boolean active;
    private final String keyboardName;
    private List<Integer> notesOn; // list of all notes, that are currently playing in the sequence

    Keyboard(File keyboardDirectory) {
        keyboardName = keyboardDirectory.getName();
        active = false;
        if (!keyboardDirectory.isDirectory() || keyboardDirectory.listFiles() == null) {
            keyboardPatterns = null;
            numberOfPatterns = 0;
            lastTick = 0;
            return;
        }
        List<File> files = Arrays.asList(keyboardDirectory.listFiles());
        numberOfPatterns = files.size();

        keyboardPatterns = files
                .stream()
                .map(f -> {
                    try {
                        return new Pattern(f.getAbsoluteFile());
                    } catch (OrganSequencerException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());


        long temp;
        temp = 0;
        for(Pattern pattern : keyboardPatterns) {
            int numberOfMidiEvents = pattern.getNumberOfMidiEvents();
            temp += pattern.getOrganEvent(numberOfMidiEvents - 1).getTick();
        }
        lastTick = temp;
        notesOn = new ArrayList<Integer>();
    }

    public List<OrganSequence> getSequences() {
        return keyboardPatterns
                .stream()
                .map(Pattern::getOrganSequence)
                .collect(Collectors.toList());
    }

    /*
    public List<Sequence> getSequences() {
        return keyboardPatterns
                .stream()
                .map(Pattern::getPatternSequence)
                .collect(Collectors.toList());
    }

     */

    /*
    public Track getFirstTrack() {
        return getSequences().getFirst().getTracks()[0];
    }



    public int getFirstTrackNumberOfEvents() {
        return getFirstTrack().size();
    }

    public List<Track> getTracks() {
        return getSequences()
                .stream()
                .map(s -> s.getTracks()[0])
                .collect(Collectors.toList());
    }

 */

    void addNoteToNotesOn(int note) {
        notesOn.add(note);
    }

    public int getResolution() throws OrganSequencerException {
        List<Integer> resolutionsList = keyboardPatterns
                .stream()
                .map(pattern -> pattern.getOrganSequence().getResolution())
               // .map(p -> p.getPatternSequence().getResolution())
                .collect(Collectors.toList());

        int firstResolution = resolutionsList.getFirst();
        for(int i=0; i<resolutionsList.size(); i++ ) {
            if(resolutionsList.get(i) != firstResolution) {
                throw new OrganSequencerException("Error in Pattern" + keyboardPatterns.get(i).getPatternName() + "!\nAll patterns in the Keyboard should have the same resolution!");
            }
        }
        return firstResolution;

    }

    public void makeActive() {
        active = true;
    }

    public void makeInactive()  {
        active = false;
    }

    public long updateLastTick() {
        long temp;
        temp = 0;
        for(Pattern pattern : keyboardPatterns) {
            int numberOfMidiEvents = pattern.getNumberOfMidiEvents();
            temp += pattern.getOrganEvent(numberOfMidiEvents - 1).getTick();
        }
        this.lastTick = temp;
        return this.lastTick;
    }
}
