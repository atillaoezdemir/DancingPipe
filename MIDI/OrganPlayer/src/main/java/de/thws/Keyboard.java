package de.thws;

import java.util.ArrayList;

import de.thws.configurators.KeyboardConfigurator;
import java.util.List;
import java.util.stream.Collectors;

import de.thws.configurators.PatternConfigurator;
import de.thws.exceptions.ConfiguratorException;
import de.thws.exceptions.OrganSequencerException;
import de.thws.helpers.ConfiguratorHelper;
import lombok.Getter;

@Getter
public class Keyboard {
    private final List<Pattern> keyboardPatterns;
    private final int numberOfPatterns;
//    private long lastTick;
    private boolean active;
    private final KeyboardName keyboardName;
    private List<Integer> notesOn; // list of all notes, that are currently playing in the sequence


    /**
     * Creates a keyboard object from KeyboardConfigurator object.
     * @param configurator KeyboardConfigurator object to be used
     * @throws OrganSequencerException
     * @throws ConfiguratorException if the keyboard name in the configurator object is invalid
     */
    public Keyboard(KeyboardConfigurator configurator) throws OrganSequencerException, ConfiguratorException {
        this.keyboardPatterns = new ArrayList<>();

        PatternConfigurator[] patternConfigurators  = configurator.getPatternConfigurators();
        for(PatternConfigurator patternConfigurator : patternConfigurators) {
            this.keyboardPatterns.add(patternConfigurator.convertToPattern());
        }
        this.numberOfPatterns = this.keyboardPatterns.size();

        notesOn = new ArrayList<Integer>();
        this.active = false;

        this.keyboardName = ConfiguratorHelper.convertStringToKeyboardName(configurator.getKeyboardName());



    }

    /*

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
                .sorted(Comparator.comparing(File::getName))
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

     */

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

    /**
     * @return resolution for the keyboard
     * @throws OrganSequencerException if some of the patterns in the keyboard has a different resolution from the other ones
     */
    public int getResolution() throws OrganSequencerException {
        List<Integer> resolutionsList = keyboardPatterns
                .stream()
                .map(pattern -> {
                    if(!pattern.isEmpty()) {
                        return  pattern.getOrganSequence().getResolution();
                    }
                    return 0;
                })
               // .map(p -> p.getPatternSequence().getResolution())
                .toList();

        return isResolutionSame(resolutionsList);
    }

    /**
     * Checks if all the Patterns have the same resolution and returns it if so.
     * @param resolutionsList list of resolutions for all patterns
     * @return resolution for all patterns
     * @throws OrganSequencerException if some of the patterns has a different resolution
     */
    private int isResolutionSame(List<Integer> resolutionsList) throws OrganSequencerException {
        int firstResolution = resolutionsList.getFirst();
        for(int i = 0; i< resolutionsList.size(); i++ ) {
            if(firstResolution == 0) { // can be if the first pattern is empty
                if(resolutionsList.get(i) != 0) {
                    firstResolution = resolutionsList.get(i);
                }
            }
            else {
                if(resolutionsList.get(i) != firstResolution && resolutionsList.get(i) != 0) {
                    throw new OrganSequencerException("Error in Pattern" + keyboardPatterns.get(i).getPatternName() + "!\nAll patterns in the Keyboard should have the same resolution!");
                }
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

       /* long temp;
        temp = 0;
        for(Pattern pattern : keyboardPatterns) {
            int numberOfMidiEvents = pattern.getNumberOfMidiEvents();
            temp += pattern.getOrganEvent(numberOfMidiEvents - 1).getTick();
        }
        this.lastTick = temp;
        return this.lastTick;


        */
        return keyboardPatterns.getLast().getOrganSequence().getEvents().getLast().getTick(); // todo not like this
    }
}
