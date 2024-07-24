package com.example.organServer.services;

import com.example.organServer.models.ToConsumerDTO;
import com.example.organServer.models.ToWebClientDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmitterServiceTest {

    private EmitterService emitterService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SseEmitter mockConsumerEmitter;

    @Mock
    private SseEmitter mockWebClientEmitter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emitterService = new EmitterService();

        setPrivateField(emitterService, "consumerEmitter", mockConsumerEmitter);
        setPrivateField(emitterService, "webClientEmitter", mockWebClientEmitter);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sendToConsumer_WhenEmitterIsActive() throws Exception {
        ToConsumerDTO message = new ToConsumerDTO(5, "START", 120);
        String jsonMessage = "{\"keyboardsInUse\":5,\"command\":\"START\",\"currentTempo\":120}";
        when(objectMapper.writeValueAsString(message)).thenReturn(jsonMessage);
        emitterService.sendToConsumer(message);
        verify(mockConsumerEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void sendToConsumer_WhenEmitterIsNotActive() {
        setPrivateField(emitterService, "consumerEmitter", null);
        ToConsumerDTO message = new ToConsumerDTO(5, "START", 120);
        assertDoesNotThrow(() -> emitterService.sendToConsumer(message));
    }

    @Test
    void sendToWebClient_ShouldSendData() throws Exception {
        ToWebClientDTO message = new ToWebClientDTO(
                3, 5, 3, "START", true, true, true, 40, "Symphony", "Mozart");
        String jsonData = "{\"keyboardsInUse\":3,\"maxAvailableKeyboards\":5,\"currentTempo\":3,\"command\":\"START\",\"wasCommandExecuted\":true,\"consumerConnected\":true,\"startCommandReceived\":true,\"barLength\":40,\"title\":\"Symphony\",\"composerName\":\"Mozart\"}";
        when(objectMapper.writeValueAsString(message)).thenReturn(jsonData);
        emitterService.sendToWebClient(message);
        verify(mockWebClientEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }


    @Test
    void hasActiveConsumerEmitters_ShouldReturnFalse_WhenEmitterIsInactive() {
        setPrivateField(emitterService, "consumerEmitter", null);
        assertFalse(emitterService.hasActiveConsumerEmitters());
    }


    @Test
    void sendToConsumer_WhenEmitterIsInactiveAndHandlesExceptions()  {
        setPrivateField(emitterService, "consumerEmitter", null);
        ToConsumerDTO message = new ToConsumerDTO(5, "START", 120);
        assertDoesNotThrow(() -> emitterService.sendToConsumer(message));
    }

    @Test
    void sendToWebClient_WhenEmitterIsInactiveAndHandlesExceptions()  {
        setPrivateField(emitterService, "webClientEmitter", null);

        ToWebClientDTO message = new ToWebClientDTO(
                3, 5, 3, "START", true, true, true, 40, "Symphony", "Mozart");

        assertDoesNotThrow(() -> emitterService.sendToWebClient(message));
    }


    @Test
    void checkNewEmitterConfiguration() {
        SseEmitter newEmitter = emitterService.addConsumerEmitter();
        assertNotNull(newEmitter);
        assertEquals(Long.MAX_VALUE, newEmitter.getTimeout());
        Runnable completion = mock(Runnable.class);
        newEmitter.onCompletion(completion);
        completion.run();
        verify(completion, times(1)).run();

    }
}
