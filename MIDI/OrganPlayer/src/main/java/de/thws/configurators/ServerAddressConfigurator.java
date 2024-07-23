package de.thws.configurators;


import lombok.Getter;
/**
 * Represents the content of a JSON file, used for saving the addresses of the server.
 * <p>Class members:
 * <ul>
 *     <li> {@code local} - local URI of the server as {@link String}.
 *     <li> {@code remote} - remote URI of the server as {@link String}.
 * </ul>
 */
@Getter
public class ServerAddressConfigurator {

    private String local;
    private String remote;

    ServerAddressConfigurator() {}
}
