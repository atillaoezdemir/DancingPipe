package com.example.loginapp.services;

import com.example.loginapp.model.ToConsumerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumberService {
    @Autowired
    private EmitterService emitterService;
    @Autowired
    private OrganSettingsService organSettingsService;

    public void sendNumber(int number) {
        emitterService.sendToWebClient(String.valueOf(number));

        ToConsumerDTO message = switch (number) {
            case 1 -> organSettingsService.incrementKeyboards();
            case 2 -> organSettingsService.decrementKeyboards();
            case 3 -> organSettingsService.useAllKeyboards();
            case 4 -> organSettingsService.useOneKeyboard();
            case 99 -> organSettingsService.sendStartCommand();
            case 100 -> organSettingsService.sendStopCommand();
            default -> null;
        };

        if (message != null) {
            emitterService.sendToConsumer(message);
        }
    }

//    public String configureMaxKeyboards(int max) {
//        return String.valueOf(organSettingsService.getMaxAvailableKeyboards());
//    }
}