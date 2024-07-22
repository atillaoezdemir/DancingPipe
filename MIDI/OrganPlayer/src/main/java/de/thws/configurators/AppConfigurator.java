package de.thws.configurators;

import lombok.Getter;

/**
 * Represents the content of a JSON file, used for saving the application details.
 * <p>Class members:
 * <ul>
 *     <li> {@code appName} - name of the application as {@link String}.
 *     <li> {@code appVersion} - version of the application as {@link String}.
 *     <li> {@code appAuthors} - authors of the application as array of {@link String}s.
 *     <li> {@code appDescription} - description of the application as {@link String}
 * </ul>
 */
@Getter
public class AppConfigurator {
    private String appName;
    private String appVersion;
    private String[] appAuthors;
    private String appDescription;

    AppConfigurator() {
    }
}
