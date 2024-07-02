package com.example.loginapp.services;

import com.example.loginapp.model.FromConsumerDTO;
import com.example.loginapp.model.ToConsumerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NumberService {
    private final EmitterService emitterService;
    private final OrganSettingsService organSettingsService;

    public void sendNumber(int number) {
        emitterService.sendToWebClient(String.valueOf(number));

        ToConsumerDTO message = switch (number) {
            case 1 -> organSettingsService.incrementKeyboards();
            case 2 -> organSettingsService.decrementKeyboards();
            case 3 -> organSettingsService.useAllKeyboards();
            case 4 -> organSettingsService.useOneKeyboard();
            case 5 -> organSettingsService.incrementTempo();
            case 6 -> organSettingsService.decrementTempo();
            case 7 -> organSettingsService.defaultTempo();
            case 99 -> organSettingsService.sendStartCommand();
            case 100 -> organSettingsService.sendStopCommand();
            default -> null;
        };

        if (message != null) {
            emitterService.sendToConsumer(message);
        }
    }
    public ResponseEntity<ToConsumerDTO> getResponse(FromConsumerDTO config) {
        organSettingsService.setMaxAvailableKeyboards(config.getKeyboardsMax());
        organSettingsService.setKeyboardsInUse(config.getDefaultKeyboards());
        ToConsumerDTO ToConsumerDTO = new ToConsumerDTO(organSettingsService.getKeyboardsInUse(), "configureMax", organSettingsService.getCurrentTempo());

        return ResponseEntity.ok(ToConsumerDTO);
    }

}