package com.example.organServer.controllers;

import com.example.organServer.models.FromConsumerDTO;
import com.example.organServer.models.ToConsumerDTO;
import com.example.organServer.services.EmitterService;
import com.example.organServer.services.NumberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    public void consumer_ShouldVerifyEmitterIsActive() throws Exception {
        SseEmitter expectedEmitter = new SseEmitter();
        when(emitterService.addConsumerEmitter()).thenReturn(expectedEmitter);

        MockHttpServletResponse response = mockMvc.perform(get("/consumer"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        verify(emitterService).addConsumerEmitter();

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
    public void adjustConfiguration_ShouldHandleUnexpectedExceptions() throws Exception {
        FromConsumerDTO fromConsumerDTO = new FromConsumerDTO();
        fromConsumerDTO.setKeyboardsMax(10);
        fromConsumerDTO.setDefaultKeyboards(5);
        fromConsumerDTO.setBarLength(15);
        fromConsumerDTO.setTitle("Symphony");
        fromConsumerDTO.setComposerName("Mozart");

        doThrow(new RuntimeException("Internal Server Error")).when(numberService)
                .getResponse(any(FromConsumerDTO.class));

        String jsonDto = objectMapper.writeValueAsString(fromConsumerDTO);

        mockMvc.perform(post("/consumer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isInternalServerError());
    }



    @Test
    public void adjustConfiguration_ShouldRejectInvalidJson() throws Exception {
        String invalidJson = "invalid json";

        mockMvc.perform(post("/consumer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void adjustConfiguration_VerifyResponseContent_WhenValidRequest() throws Exception {
        FromConsumerDTO fromConsumerDTO = new FromConsumerDTO();
        fromConsumerDTO.setKeyboardsMax(10);
        fromConsumerDTO.setDefaultKeyboards(5);
        fromConsumerDTO.setBarLength(15);
        fromConsumerDTO.setTitle("Symphony");
        fromConsumerDTO.setComposerName("Mozart");

        ToConsumerDTO toConsumerDTO = new ToConsumerDTO(5, "update", 120);
        when(numberService.getResponse(any(FromConsumerDTO.class))).thenReturn(ResponseEntity.ok(toConsumerDTO));

        String jsonDto = objectMapper.writeValueAsString(fromConsumerDTO);
        String expectedResponseContent = objectMapper.writeValueAsString(toConsumerDTO);

        mockMvc.perform(post("/consumer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseContent));
    }

}

