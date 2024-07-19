package de.thws.configurators;
import de.thws.OrganSequencerException;
import de.thws.Pattern;
import de.thws.helpers.PatternHelper;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.Serializable;

@Getter
public class PatternConfigurator implements Serializable {
    int patternNumber;
    String patternFile;
    boolean beginsWithTie;
    boolean endsWithTie;
    boolean canBeInterrupted;

    public PatternConfigurator() {}

    public PatternConfigurator (int patternNumber, String patternFile, boolean beginsWithTie, boolean endsWithTie, boolean canBeInterrupted) throws OrganSequencerException {
        this.patternNumber = patternNumber;
        this.patternFile = patternFile;
        this.beginsWithTie = beginsWithTie;
        this.endsWithTie = endsWithTie;
        this.canBeInterrupted = canBeInterrupted;
    }

    /**
     * Converts the current file given in the configurator to pattern.
     * @return object of type Pattern
     * @throws OrganSequencerException
     */
    public Pattern convertToPattern() throws OrganSequencerException {
        Pattern result;
        if(this.patternFile.isEmpty()) {
            // pattern is empty
            result = new Pattern();
        }
        else {
            result = new Pattern(new File(this.patternFile));
            result.setPatternIndex(this.patternNumber);
            if(this.beginsWithTie) {
                if(PatternHelper.deleteFirstNoteOnEvent(result.getOrganSequence())) {
                    int numberOfMidiEvents = result.getNumberOfMidiEvents() - 1;
                    result.setNumberOfMidiEvents(numberOfMidiEvents);
                }

            }
            if(this.endsWithTie) {
                if(PatternHelper.deleteLastNoteOffEvent(result.getOrganSequence())) {
                    int numberOfMidiEvents = result.getNumberOfMidiEvents() - 1;
                    result.setNumberOfMidiEvents(numberOfMidiEvents);
                }
            }
            if(this.canBeInterrupted) {
                result.setCanBeInterrupted(this.canBeInterrupted);
            }

        }
        return result;

    }
}
