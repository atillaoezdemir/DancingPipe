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
        if (emitterService.hasActiveConsumerEmitters()) {
            this.keyboardsInUse = keyboards;
        }
    }


    public void setMaxAvailableKeyboards(int keyboards) {
        if (emitterService.hasActiveConsumerEmitters()) {
            this.maxAvailableKeyboards = keyboards;
        }

    }

    public DTOWrapper sendStartCommand() {
        if (!startCommandReceived) {
            setCurrentTempo(3);
            startCommandReceived = true;
            setCommand("start");
            setWasCommandExecuted(true);
            return getCurrentValues();
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper sendStopCommand() {
        if (startCommandReceived) {
            setCurrentTempo(3);
            setKeyboardsInUse(0);
            setMaxAvailableKeyboards(0);
            startCommandReceived = false;
            setCommand("stop");
            setWasCommandExecuted(true);
            return getCurrentValues();
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper incrementKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                keyboardsInUse++;
                setCommand("incrementKeyboards");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper decrementKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                keyboardsInUse--;
                setCommand("decrementKeyboards");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper useOneKeyboard() {
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                keyboardsInUse = 1;
                setCommand("minKeyboards");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper useAllKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                keyboardsInUse = maxAvailableKeyboards;
                setCommand("maxKeyboards");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper incrementTempo() {
        if (startCommandReceived) {
            if (currentTempo < maxTempo) {
                currentTempo++;
                setCommand("incrementTempo");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper decrementTempo() {
        if (startCommandReceived) {
            if (currentTempo > minTempo) {
                currentTempo--;
                setCommand("decrementTempo");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    public DTOWrapper defaultTempo() {
        if (startCommandReceived) {
            if (currentTempo != defaultTempo) {
                currentTempo = defaultTempo;
                setCommand("defaultTempo");
                setWasCommandExecuted(true);
                return getCurrentValues();
            }
        }
        setWasCommandExecuted(false);
        return getCurrentValues();
    }

    private DTOWrapper getCurrentValues() {
        ToWebClientDTO toWebClientDTO;
        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(getKeyboardsInUse(), getCommand(), getCurrentTempo());
        if (emitterService.hasActiveConsumerEmitters()) {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, isWasCommandExecuted(),true, isStartCommandReceived(),getBarLength(),
                    getTitle(), getComposerName());
        } else {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, false,false, isStartCommandReceived(),getBarLength(),
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
