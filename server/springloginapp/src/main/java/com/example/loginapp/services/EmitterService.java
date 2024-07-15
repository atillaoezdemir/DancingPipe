package com.example.loginapp.services;

import com.example.loginapp.models.ToConsumerDTO;
import com.example.loginapp.models.ToWebClientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class EmitterService {
    private SseEmitter consumerEmitter;
    private SseEmitter webClientEmitter;
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

    public void sendToWebClient(ToWebClientDTO message) {
        try {
            webClientEmitter.send(SseEmitter.event().data(message));
        } catch (Exception e) {
            webClientEmitter = null;
        }
    }


    public boolean hasActiveConsumerEmitters() {
        return consumerEmitter != null;
    }


}
