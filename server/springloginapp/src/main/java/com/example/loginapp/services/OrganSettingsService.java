package com.example.loginapp.services;

import com.example.loginapp.models.DTOWrapper;
import com.example.loginapp.models.FromConsumerDTO;
import com.example.loginapp.models.ToConsumerDTO;
import com.example.loginapp.models.ToWebClientDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

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
    private boolean wasCommandExecuted = false;


    public void setKeyboardsInUse(int keyboards) {
        if (isConsumerOnline()) {
            this.keyboardsInUse = keyboards;
        }
    }


    public void setMaxAvailableKeyboards(int keyboards) {
        if (isConsumerOnline()) {
            this.maxAvailableKeyboards = keyboards;
        }

    }

    public DTOWrapper sendStartCommand() {
        setCommand("start");
        if (isConsumerOnline() && !startCommandReceived) {
            setCurrentTempo(3);
            startCommandReceived = true;
            setWasCommandExecuted(true);
            return getCurrentValues();
        }

        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper sendStopCommand() {
        setCommand("stop");
        if (isConsumerOnline() && startCommandReceived) {
            setCurrentTempo(3);
            setKeyboardsInUse(0);
            setMaxAvailableKeyboards(0);
            startCommandReceived = false;
            setWasCommandExecuted(true);
            return getCurrentValues();
        }
        setWasCommandExecuted(false);

        return getCurrentValues();
    }

    public DTOWrapper incrementKeyboards() {
        setCommand("incrementKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                if (isConsumerOnline()) {
                    keyboardsInUse++;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper decrementKeyboards() {
        setCommand("decrementKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                if (isConsumerOnline()) {
                    keyboardsInUse--;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper useOneKeyboard() {
        setCommand("minKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                if (isConsumerOnline()) {
                    keyboardsInUse = 1;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper useAllKeyboards() {
        setCommand("maxKeyboards");
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                if (isConsumerOnline()) {
                    keyboardsInUse = maxAvailableKeyboards;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper incrementTempo() {
        setCommand("incrementTempo");
        if (startCommandReceived) {
            if (currentTempo < maxTempo) {
                if (isConsumerOnline()) {
                    currentTempo++;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper decrementTempo() {
        setCommand("decrementTempo");
        if (startCommandReceived) {
            if (currentTempo > minTempo) {
                if (isConsumerOnline()) {
                    currentTempo--;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper defaultTempo() {
        setCommand("defaultTempo");
        if (startCommandReceived) {
            if (currentTempo != defaultTempo) {
                if (isConsumerOnline()) {
                    currentTempo = defaultTempo;
                }
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    private boolean isConsumerOnline() {
        return emitterService.hasActiveConsumerEmitters();
    }

    private DTOWrapper getCurrentValues() {
        ToWebClientDTO toWebClientDTO;
        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(getKeyboardsInUse(), getCommand(), getCurrentTempo());
        if (isConsumerOnline()) {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, isWasCommandExecuted(), true, isStartCommandReceived(), getBarLength(),
                    getTitle(), getComposerName());
        } else {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, false, false, isStartCommandReceived(), getBarLength(),
                    getTitle(), getComposerName());
        }

        return new DTOWrapper(toConsumerDTO, toWebClientDTO);
    }

    public void updateState(FromConsumerDTO config) {
        setMaxAvailableKeyboards(config.getKeyboardsMax());
        setKeyboardsInUse(config.getDefaultKeyboards());
        setTitle(config.getTitle());
        setBarLength(config.getBarLength());
        setComposerName(config.getComposerName());
        setMessage(getCurrentValues().getToWebClientDTO());
    }
}
