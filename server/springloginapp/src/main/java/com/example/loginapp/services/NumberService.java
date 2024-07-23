package com.example.loginapp.services;

import com.example.loginapp.enums.Action;
import com.example.loginapp.models.DTOWrapper;
import com.example.loginapp.models.FromConsumerDTO;
import com.example.loginapp.models.ToConsumerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class NumberService {
    private final EmitterService emitterService;
    private final OrganSettingsService organSettingsService;



    public void sendNumber(int number) {
        Action action = Action.getAction(number);
        DTOWrapper message = switch (action) {
            case INCREMENT_KEYBOARDS -> organSettingsService.incrementKeyboards();
            case DECREMENT_KEYBOARDS -> organSettingsService.decrementKeyboards();
            case USE_ALL_KEYBOARDS -> organSettingsService.useAllKeyboards();
            case USE_ONE_KEYBOARD -> organSettingsService.useOneKeyboard();
            case INCREMENT_TEMPO -> organSettingsService.incrementTempo();
            case DECREMENT_TEMPO -> organSettingsService.decrementTempo();
            case DEFAULT_TEMPO -> organSettingsService.defaultTempo();
            case SEND_START_COMMAND -> organSettingsService.sendStartCommand();
            case SEND_STOP_COMMAND -> organSettingsService.sendStopCommand();
            case UNDEFINED -> null;
        };

        if (message != null) {
            updateAndSend(message);
        }
    }

    private void updateAndSend(DTOWrapper message) {

        if (emitterService.hasActiveConsumerEmitters()) {
            emitterService.sendToConsumer(message.getToConsumerDTO());
        }
        if (sendCheck(message)) {
            emitterService.sendToWebClient(message.getToWebClientDTO());
        }

    }

    private static boolean sendCheck(DTOWrapper message) {
        return !message.getToWebClientDTO().consumerConnected() ||
                (!Objects.equals(message.getToWebClientDTO().command(), "start") &&
                        !Objects.equals(message.getToWebClientDTO().command(), "stop"));
    }

    public ResponseEntity<ToConsumerDTO> getResponse(FromConsumerDTO config) {
        organSettingsService.updateState(config);
        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(organSettingsService.getKeyboardsInUse(),
                "configurationResponse", organSettingsService.getCurrentTempo());
        emitterService.sendToWebClient(organSettingsService.getMessage());
        return ResponseEntity.ok(toConsumerDTO);
    }

}