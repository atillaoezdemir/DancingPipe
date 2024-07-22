package com.example.loginapp.services;

import com.example.loginapp.models.ToConsumerDTO;
import com.example.loginapp.models.ToWebClientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

//This service responsible for managing Server-Sent Events (SSE) emitters for different types of clients
@Service
@Getter
@Setter
public class EmitterService {
    private SseEmitter consumerEmitter;
    private SseEmitter webClientEmitter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Initializes and returns a new SSE emitter for "Organ Sequencer" client.
    //Returns: SseEmitter - a new emitter instance configured with maximum timeout
    //and event handlers for completion, timeout, and error.
    public SseEmitter addConsumerEmitter() {
        consumerEmitter = new SseEmitter(Long.MAX_VALUE);
        consumerEmitter.onCompletion(() -> consumerEmitter = null);
        consumerEmitter.onTimeout(() -> consumerEmitter = null);
        consumerEmitter.onError(e -> consumerEmitter = null);
        return consumerEmitter;
    }
    //Initializes and returns a new SSE emitter for a frontend client.
    //Returns: SseEmitter - a new emitter instance configured with maximum timeout
    //and event handlers for completion, timeout, and error.
    public SseEmitter addWebClientEmitter() {
        webClientEmitter = new SseEmitter(Long.MAX_VALUE);
        webClientEmitter.onCompletion(() -> webClientEmitter = null);
        webClientEmitter.onTimeout(() -> webClientEmitter = null);
        webClientEmitter.onError(e -> webClientEmitter = null);
        return webClientEmitter;
    }


    //Sends a message to the "Organ Sequencer" client via SSE.
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

    //Sends a message to a frontend client via SSE.
    public void sendToWebClient(ToWebClientDTO message) {
        try {
            webClientEmitter.send(SseEmitter.event().data(message));
        } catch (Exception e) {
            webClientEmitter = null;
        }
    }

    //Checks if there are any active consumer emitters.
    //
    public boolean hasActiveConsumerEmitters() {
        return consumerEmitter != null;
    }


}
