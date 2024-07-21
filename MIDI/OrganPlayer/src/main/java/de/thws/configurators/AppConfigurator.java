package de.thws.configurators;

import lombok.Getter;

@Getter
public class AppConfigurator {
    private String appName;
    private String appVersion;
    private String[] appAuthors;
    private String appDescription;

    AppConfigurator() {}
}
