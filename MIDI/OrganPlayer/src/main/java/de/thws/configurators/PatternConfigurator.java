package de.thws.configurators;
import de.thws.exceptions.OrganSequencerException;
import de.thws.components.Pattern;
import de.thws.helpers.PatternHelper;
import lombok.Getter;

import java.io.File;
import java.io.Serializable;

/**
 * Used for reading configuration information about a single pattern from a JSON file.
 * <p>Class members:
 * <ul>
 *     <li> {@code compositionName} - title of the composition as {@link String}.
 *     <li> {@code composer} - composer of the composition as {@link String}.
 *     <li> {@code lengthInBars} - length of the composition in bars as {@code long}.
 *     <li> {@code tempoFactor} - the tempo factor, which is used when changing the tempo of the composition as {@code float}.
 * </ul>
 */
@Getter
public class PatternConfigurator implements Serializable {
    int patternNumber;
    String patternFile;
    boolean beginsWithTie;
    boolean endsWithTie;
    boolean canBeInterrupted;

    public PatternConfigurator() {}

    public PatternConfigurator (int patternNumber, String patternFile, boolean beginsWithTie, boolean endsWithTie, boolean canBeInterrupted) {
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
        if(!(this.patternFile.contains(".mid") || this.patternFile.contains(".midi"))) {
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
                result.setCanBeInterrupted(true);
            }

        }
        return result;

    }
}
