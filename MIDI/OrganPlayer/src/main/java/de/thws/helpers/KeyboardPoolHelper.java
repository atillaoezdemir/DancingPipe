package de.thws.helpers;

import de.thws.Keyboard;
import de.thws.OrganSequencerException;

import java.util.List;

public class KeyboardPoolHelper {

    /**
     * Checks if all the keyboards in the list have the same resolution and returns it.
     * @return resolution of all keyboards
     * @throws OrganSequencerException if not all keyboards have the same resolution
     */
    public static int getKeyboardPoolResolution (List<Keyboard> keyboardList) throws OrganSequencerException {
        int firstResolution = keyboardList.getFirst().getResolution();
        for (Keyboard keyboard : keyboardList) {
            if (keyboard.getResolution() != firstResolution) {
                throw new OrganSequencerException("Error in Keyboard " + keyboard.getKeyboardName() + "!\nAll Keyboards should have the same resolution!");
            }
        }
        return firstResolution;
    }
}
