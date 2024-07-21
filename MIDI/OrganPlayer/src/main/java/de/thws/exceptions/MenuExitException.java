package de.thws.exceptions;

public class MenuExitException extends Exception {
    public MenuExitException(String errorMessage) {
        super(errorMessage);
    }
}
