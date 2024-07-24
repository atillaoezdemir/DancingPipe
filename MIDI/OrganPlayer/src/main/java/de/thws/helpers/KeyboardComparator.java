package de.thws.helpers;

import de.thws.components.Keyboard;

import java.util.Comparator;

/**
 * Comparator class for comparing two keyboards, based on the order of playing of the keyboards.
 * @see Keyboard
 */
public class KeyboardComparator implements Comparator<Keyboard>  {
    /**
     * Method to use when comparing two keyboards. The keyboards are compared based on which should be played first.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     */
    @Override
    public int compare(Keyboard o1, Keyboard o2) {
        return Integer.compare(o1.getKeyboardName().getOrderToPlay(), o2.getKeyboardName().getOrderToPlay());
    }

}
