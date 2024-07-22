package com.example.loginapp.controllers;

import com.example.loginapp.models.FromConsumerDTO;
import com.example.loginapp.models.ToConsumerDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.NumberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsumerController.class)
public class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmitterService emitterService;

    @MockBean
    private NumberService numberService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void consumer_ShouldReturnEmitter_WhenSuccessful() throws Exception {
        when(emitterService.addConsumerEmitter()).thenReturn(new SseEmitter());

        mockMvc.perform(get("/consumer"))
                .andExpect(status().isOk());
    }

    @Test
    public void consumer_ShouldReturnError_WhenExceptionThrown() throws Exception {
        when(emitterService.addConsumerEmitter()).thenThrow(new RuntimeException("Emitter error"));

        mockMvc.perform(get("/consumer"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void adjustConfiguration_ShouldReturnResponseEntity_WhenValidRequest() throws Exception {
        FromConsumerDTO fromConsumerDTO = new FromConsumerDTO();
        fromConsumerDTO.setKeyboardsMax(10);
        fromConsumerDTO.setDefaultKeyboards(5);
        fromConsumerDTO.setBarLength(15);
        fromConsumerDTO.setTitle("Symphony");
        fromConsumerDTO.setComposerName("Mozart");

        ToConsumerDTO responseDto = new ToConsumerDTO(10, "Success", 100);
        when(numberService.getResponse(any(FromConsumerDTO.class))).thenReturn(ResponseEntity.ok(responseDto));

        String jsonDto = objectMapper.writeValueAsString(fromConsumerDTO);

        mockMvc.perform(post("/consumer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    public void adjustConfiguration_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        FromConsumerDTO fromConsumerDTO = new FromConsumerDTO();
        fromConsumerDTO.setKeyboardsMax(10);
        fromConsumerDTO.setDefaultKeyboards(5);
        fromConsumerDTO.setBarLength(15);
        fromConsumerDTO.setTitle("Concerto");
        fromConsumerDTO.setComposerName("Bach");

        when(numberService.getResponse(any(FromConsumerDTO.class))).thenThrow(new RuntimeException("Service error"));

        String jsonDto = objectMapper.writeValueAsString(fromConsumerDTO);

        mockMvc.perform(post("/consumer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error: Service error")));
    }

    @Test
    public void adjustConfiguration_ThrowsConstraintViolationException_ShouldReturnBadRequest() throws Exception {
        FromConsumerDTO fromConsumerDTO = new FromConsumerDTO();
        fromConsumerDTO.setKeyboardsMax(5);
        fromConsumerDTO.setDefaultKeyboards(5);
        fromConsumerDTO.setBarLength(10);

        when(numberService.getResponse(any(FromConsumerDTO.class))).thenThrow(ConstraintViolationException.class);

        String jsonDto = objectMapper.writeValueAsString(fromConsumerDTO);

        mockMvc.perform(post("/consumer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isBadRequest());
    }
}
