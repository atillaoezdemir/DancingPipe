package com.example.loginapp.services;

import com.example.loginapp.models.DTOWrapper;
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
            return getCurrentValues("start", true);
        }
        return getCurrentValues("start", false);
    }

    public DTOWrapper sendStopCommand() {
        if (startCommandReceived) {
            setCurrentTempo(3);
            setKeyboardsInUse(0);
            setMaxAvailableKeyboards(0);
            startCommandReceived = false;
            return getCurrentValues("stop", true);
        }
        return getCurrentValues("stop", false);
    }

    public DTOWrapper incrementKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                keyboardsInUse++;
                return getCurrentValues("incrementKeyboards", true);
            }
        }
        return getCurrentValues("incrementKeyboards", false);
    }

    public DTOWrapper decrementKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                keyboardsInUse--;
                return getCurrentValues("decrementKeyboards", true);
            }
        }
        return getCurrentValues("decrementKeyboards", false);
    }

    public DTOWrapper useOneKeyboard() {
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                keyboardsInUse = 1;
                return getCurrentValues("minKeyboards", true);
            }
        }
        return getCurrentValues("minKeyboards", false);
    }

    public DTOWrapper useAllKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                keyboardsInUse = maxAvailableKeyboards;
                return getCurrentValues("maxKeyboards", true);
            }
        }
        return getCurrentValues("maxKeyboards", false);
    }

    public DTOWrapper incrementTempo() {
        if (startCommandReceived) {
            if (currentTempo < maxTempo) {
                currentTempo++;
                return getCurrentValues("incrementTempo", true);
            }
        }
        return getCurrentValues("incrementTempo", false);
    }

    public DTOWrapper decrementTempo() {
        if (startCommandReceived) {
            if (currentTempo > minTempo) {
                currentTempo--;
                return getCurrentValues("decrementTempo", true);
            }
        }
        return getCurrentValues("decrementTempo", false);
    }

    public DTOWrapper defaultTempo() {
        if (startCommandReceived) {
            if (currentTempo != defaultTempo) {
                currentTempo = defaultTempo;
                return getCurrentValues("defaultTempo", true);
            }
        }
        return getCurrentValues("defaultTempo", false);
    }


    private DTOWrapper getCurrentValues(String command, boolean wasCommandExecuted) {
        ToWebClientDTO toWebClientDTO;
        if (emitterService.hasActiveConsumerEmitters()) {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, wasCommandExecuted,true, isStartCommandReceived(),getBarLength(),
                    getTitle(), getComposerName());
        } else {
            toWebClientDTO = new ToWebClientDTO(getKeyboardsInUse(), getMaxAvailableKeyboards(), getCurrentTempo(),
                    command, false,false, isStartCommandReceived(),getBarLength(),
                    getTitle(), getComposerName());
        }
        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(getKeyboardsInUse(), command, getCurrentTempo());

        return new DTOWrapper(toConsumerDTO, toWebClientDTO);
    }
}
