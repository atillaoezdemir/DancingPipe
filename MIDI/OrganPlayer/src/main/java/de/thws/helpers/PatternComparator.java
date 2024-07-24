package de.thws.helpers;
import de.thws.components.Pattern;
import java.util.Comparator;

/**
 * Comparator class for comparing two patterns, based on their indexes.
 * @see de.thws.components.Pattern
 */
public class PatternComparator implements Comparator<Pattern> {
    /**
     * Method to use when comparing two patterns. The patterns are compared based on their indexes.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     */
    @Override
    public int compare(Pattern o1, Pattern o2) {
        return Integer.compare(o1.getPatternIndex(), o2.getPatternIndex());
    }
}
