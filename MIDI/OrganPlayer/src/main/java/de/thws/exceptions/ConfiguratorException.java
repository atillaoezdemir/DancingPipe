package de.thws.exceptions;

/**
 * Exception to be thrown by errors when configuring the application.
 * @see Exception
 */
public class ConfiguratorException extends Exception {
    public ConfiguratorException(String errorMessage) { super(errorMessage); }
}
