package com.example.organServer.services;

import com.example.organServer.models.DTOWrapper;
import com.example.organServer.models.FromConsumerDTO;
import com.example.organServer.models.ToConsumerDTO;
import com.example.organServer.models.ToWebClientDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

//This service responsible for managing the state and settings of an organ simulator.
@Getter
@Setter
@RequiredArgsConstructor
@Service
public class OrganSettingsService {

    @Setter(AccessLevel.NONE)
    private int keyboardsInUse = 0;
    @Setter(AccessLevel.NONE)
    private int maxAvailableKeyboards = 0;
    private boolean startCommandReceived = false;
    private final EmitterService emitterService;
    private int minTempo = 1;
    private int maxTempo = 5;
    private int currentTempo = -1;
    private int defaultTempo = 3;
    private int barLength = -1;
    private String title = "stopped";
    private String composerName = "stopped";
    private ToWebClientDTO message = null;
    private String command = null;
    private boolean commandExecuted = false;

//Sets the number of keyboards in use if there are active consumer emitters.
    public void setKeyboardsInUse(int keyboards) {
        if (isConsumerOnline()) {
            this.keyboardsInUse = keyboards;
        }
    }

//Sets the maximum number of available keyboards if there are active consumer emitters.
    public void setMaxAvailableKeyboards(int keyboards) {
        if (isConsumerOnline()) {
            this.maxAvailableKeyboards = keyboards;
        }

    }
//Handles the start command and updates the state accordingly
    public DTOWrapper sendStartCommand() {
        setCommand("start");
        if (isConsumerOnline() && !startCommandReceived) {
            setCurrentTempo(3);
            startCommandReceived = true;
            setCommandExecuted(true);
            return getCurrentValues();
        }

        setCommandExecuted(false);
        return getCurrentValues();
    }
//Handles the stop command and updates the state accordingly.
    public DTOWrapper sendStopCommand() {
        setCommand("stop");
        if (isConsumerOnline() && startCommandReceived) {
            setCurrentTempo(3);
            setKeyboardsInUse(0);
            setMaxAvailableKeyboards(0);
            startCommandReceived = false;
            setCommandExecuted(true);
            return getCurrentValues();
        }
        setCommandExecuted(false);

        return getCurrentValues();
    }
//Increments the number of keyboards in use
//if the start command was received and the maximum number of keyboards is not exceeded.
    public DTOWrapper incrementKeyboards() {
        setCommand("incrementKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                if (isConsumerOnline()) {
                    keyboardsInUse++;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Decrements the number of keyboards in use if the start command was received and more than one keyboard is in use.
    public DTOWrapper decrementKeyboards() {
        setCommand("decrementKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                if (isConsumerOnline()) {
                    keyboardsInUse--;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Sets the number of keyboards in use to one if the start command was received and more than one keyboard is in use.
    public DTOWrapper useOneKeyboard() {
        setCommand("minKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                if (isConsumerOnline()) {
                    keyboardsInUse = 1;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Sets the number of keyboards in use to the maximum available
//if the start command was received and not all keyboards are in use.
    public DTOWrapper useAllKeyboards() {
        setCommand("maxKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                if (isConsumerOnline()) {
                    keyboardsInUse = maxAvailableKeyboards;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Increments the tempo if the start command was received and the maximum tempo is not exceeded.
    public DTOWrapper incrementTempo() {
        setCommand("incrementTempo");
        if (startCommandReceived) {
            if (currentTempo < maxTempo) {
                if (isConsumerOnline()) {
                    currentTempo++;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Decrements the tempo if the start command was received and the minimum tempo is not exceeded.
    public DTOWrapper decrementTempo() {
        setCommand("decrementTempo");
        if (startCommandReceived) {
            if (currentTempo > minTempo) {
                if (isConsumerOnline()) {
                    currentTempo--;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Sets the tempo to the default value if the start command was received and the current tempo is not the default.
    public DTOWrapper defaultTempo() {
        setCommand("defaultTempo");
        if (startCommandReceived) {
            if (currentTempo != defaultTempo) {
                if (isConsumerOnline()) {
                    currentTempo = defaultTempo;
                }
                setCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setCommandExecuted(false);
        return getCurrentValues();
    }
//Checks if there are any active consumer emitters connected,
//indicating whether Organ Sequencer is connected and interacting with the server.
    private boolean isConsumerOnline() {
        return emitterService.hasActiveConsumerEmitters();
    }
//Retrieves the current state of the organ settings, wrapping them in a DTOWrapper.
    private DTOWrapper getCurrentValues() {
        ToWebClientDTO toWebClientDTO;
        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(getKeyboardsInUse(), getCommand(), getCurrentTempo());
        if (isConsumerOnline()) {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, isCommandExecuted(), true, isStartCommandReceived(), getBarLength(),
                    getTitle(), getComposerName());
        } else {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, false, false, isStartCommandReceived(), getBarLength(),
                    getTitle(), getComposerName());
        }

        return new DTOWrapper(toConsumerDTO, toWebClientDTO);
    }
//Updates the state of the organ settings based on the provided configuration.
    public void updateState(FromConsumerDTO config) {
        setMaxAvailableKeyboards(config.getKeyboardsMax());
        setKeyboardsInUse(config.getDefaultKeyboards());
        setTitle(config.getTitle());
        setBarLength(config.getBarLength());
        setComposerName(config.getComposerName());
        setMessage(getCurrentValues().getToWebClientDTO());
    }
}
