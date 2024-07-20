package de.thws.configurators;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thws.Configurator;
import de.thws.helpers.ConfiguratorHelper;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

@Getter
public class CompositionConfigurator implements Serializable {
    private String compositionName;
    private String composer;
    private long lengthInBars;
    private float tempoFactor;

    CompositionConfigurator() {}
}
