package com.example.organServer.services;

import com.example.organServer.enums.Action;
import com.example.organServer.models.DTOWrapper;
import com.example.organServer.models.FromConsumerDTO;
import com.example.organServer.models.ToConsumerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

//This service processes numerical commands from “Camera” component, updates organ settings, and communicates with clients via Server-Sent Events (SSE).
@RequiredArgsConstructor
@Service
public class NumberService {
    private final EmitterService emitterService;
    private final OrganSettingsService organSettingsService;


//Determines the action based on the number using the Action enum
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
//Sends the message to the consumer and web client emitters if it is needed.
    private void updateAndSend(DTOWrapper message) {

        if (emitterService.hasActiveConsumerEmitters()) {
            emitterService.sendToConsumer(message.getToConsumerDTO());
        }
        if (sendCheck(message)) {
            emitterService.sendToWebClient(message.getToWebClientDTO());
        }

    }
//Checks whether the message should be sent to the web client.
    private static boolean sendCheck(DTOWrapper message) {
        return !message.getToWebClientDTO().consumerConnected() ||
                (!Objects.equals(message.getToWebClientDTO().command(), "start") &&
                        !Objects.equals(message.getToWebClientDTO().command(), "stop"));
    }
//Updates organ settings based on the provided configuration and returns the updated settings
// to the response to the "Organ Sequencer" client.
    public ResponseEntity<ToConsumerDTO> getResponse(FromConsumerDTO config) {
        organSettingsService.updateState(config);
        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(organSettingsService.getKeyboardsInUse(),
                "configurationResponse", organSettingsService.getCurrentTempo());
        emitterService.sendToWebClient(organSettingsService.getMessage());
        return ResponseEntity.ok(toConsumerDTO);
    }

}