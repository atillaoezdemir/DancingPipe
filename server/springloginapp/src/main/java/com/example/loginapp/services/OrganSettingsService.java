package com.example.loginapp.services;

import com.example.loginapp.model.ToConsumerDTO;
import com.example.loginapp.model.WebClientDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class OrganSettingsService {

    @Setter(AccessLevel.NONE)
    private int keyboardsInUse = 0;
    @Setter(AccessLevel.NONE)
    private int maxAvailableKeyboards = 0;
    private boolean startCommandReceived = false;

    @Autowired
    private EmitterService emitterService;

    public void setKeyboardsInUse(int keyboards) {
        if (emitterService.hasActiveConsumerEmitters()) {
            this.keyboardsInUse = keyboards;
            broadcastSettings();
        }
    }


    public void setMaxAvailableKeyboards(int keyboards) {
        if (emitterService.hasActiveConsumerEmitters()) {
            this.maxAvailableKeyboards = keyboards;
            broadcastSettings();
        }

    }

    public ToConsumerDTO sendStartCommand() {
        if (!startCommandReceived) {
            startCommandReceived = true;
            return getCurrentValuesConsumer("start");
        }
        return null;
    }

    public ToConsumerDTO sendStopCommand() {
        if (startCommandReceived) {
            startCommandReceived = false;
            return getCurrentValuesConsumer("stop");
        }
        return null;
    }

    public ToConsumerDTO incrementKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                keyboardsInUse++;
                return getCurrentValuesConsumer("incrementKeyboards");
            }
        }
        return null;
    }

    public ToConsumerDTO decrementKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                keyboardsInUse--;
                return getCurrentValuesConsumer("decrementKeyboards");
            }
        }
        return null;
    }

    public ToConsumerDTO useOneKeyboard() {
        if (startCommandReceived) {
            if (keyboardsInUse > 1) {
                keyboardsInUse = 1;
                return getCurrentValuesConsumer("minKeyboards");
            }
        }
        return null;
    }

    public ToConsumerDTO useAllKeyboards() {
        if (startCommandReceived) {
            if (keyboardsInUse < maxAvailableKeyboards) {
                keyboardsInUse = maxAvailableKeyboards;
                return getCurrentValuesConsumer("maxKeyboards");
            }
        }
        return null;
    }


    private ToConsumerDTO getCurrentValuesConsumer(String command) {
        return new ToConsumerDTO(keyboardsInUse, command);
    }

    private void broadcastSettings() {
        WebClientDTO update = new WebClientDTO(keyboardsInUse, maxAvailableKeyboards);
        emitterService.sendToSettings(update);
    }

    private String getCurrentValuesWeb() {
        if (startCommandReceived) {
            return String.format("Number:%d,maxAvailableKeyboards:%d,status:%s", keyboardsInUse, maxAvailableKeyboards, "Activated");
        }
        return String.format("keyboardsInUse:%d,maxAvailableKeyboards:%d,status:%s", keyboardsInUse, maxAvailableKeyboards, "Ignored");
    }
}
