package com.example.loginapp.services;

import com.example.loginapp.model.ToConsumerDTO;
import com.example.loginapp.model.ToWebClientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class EmitterService {
    private SseEmitter consumerEmitter;
    private SseEmitter webClientEmitter;
//    private SseEmitter settingsEmitter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter addConsumerEmitter() {
        consumerEmitter = new SseEmitter(Long.MAX_VALUE);
        consumerEmitter.onCompletion(() -> consumerEmitter = null);
        consumerEmitter.onTimeout(() -> consumerEmitter = null);
        consumerEmitter.onError(e -> consumerEmitter = null);
        return consumerEmitter;
    }
    public SseEmitter addWebClientEmitter1() {
        webClientEmitter = new SseEmitter(Long.MAX_VALUE);
        webClientEmitter.onCompletion(() -> webClientEmitter = null);
        webClientEmitter.onTimeout(() -> webClientEmitter = null);
        webClientEmitter.onError(e -> webClientEmitter = null);
        return webClientEmitter;
    }

//    public SseEmitter addWebClientEmitter() {
//        webClientEmitter = new SseEmitter(Long.MAX_VALUE);
//        webClientEmitter.onCompletion(() -> webClientEmitter = null);
//        webClientEmitter.onTimeout(() -> webClientEmitter = null);
//        webClientEmitter.onError(e -> webClientEmitter = null);
//        return webClientEmitter;
//    }
//    public SseEmitter addSettingsEmitter() {
//        System.out.println("SettingsEmitter created");
//        settingsEmitter = new SseEmitter(Long.MAX_VALUE);
//        settingsEmitter.onCompletion(() -> settingsEmitter = null);
//        settingsEmitter.onTimeout(() -> settingsEmitter = null);
//        settingsEmitter.onError(e -> settingsEmitter = null);
//        return settingsEmitter;
//    }
    //todo try to extract this
//    private void setupEmitter(SseEmitter emitter) {
//        emitter.onCompletion(() -> emitter = null);
//        emitter.onTimeout(() -> emitter = null);
//        emitter.onError(e -> emitter = null);
//    }

    public void sendToConsumer(ToConsumerDTO message) {
        if (consumerEmitter != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                consumerEmitter.send(SseEmitter.event().data(jsonMessage));
            } catch (Exception e) {
                consumerEmitter = null;
            }
        }
    }

    public void sendToWebClient1(ToWebClientDTO message) {
        try {
            webClientEmitter.send(SseEmitter.event().data(message));
        } catch (Exception e) {
            webClientEmitter = null;
        }
    }
//    public void sendToSettings(WebClientDTO message) {
//        if (settingsEmitter != null) {
//            try {
//                String jsonMessage = objectMapper.writeValueAsString(message);
//                settingsEmitter.send(SseEmitter.event().data(jsonMessage));
//            } catch (Exception e) {
//                settingsEmitter = null;
//            }
//        }
//    }

    public boolean hasActiveConsumerEmitters() {
        return consumerEmitter != null;
    }

    public boolean hasActiveWebClientEmitters() {
        return webClientEmitter != null;
    }

}
