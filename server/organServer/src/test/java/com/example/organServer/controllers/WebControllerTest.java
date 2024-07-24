package com.example.organServer.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.organServer.models.UserCredentialsDTO;
import com.example.organServer.services.EmitterService;
import com.example.organServer.services.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginService loginService;

    @Mock
    private EmitterService emitterService;

    @InjectMocks
    private WebController webController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    public void testStreamWebNumbers_Success() throws Exception {
        SseEmitter expectedEmitter = new SseEmitter();
        when(emitterService.addWebClientEmitter()).thenReturn(expectedEmitter);

        mockMvc.perform(get("/web"))
                .andExpect(status().isOk());
        verify(emitterService).addWebClientEmitter();
    }
    @Test
    public void streamWebNumbers_ShouldReturnError_WhenEmitterServiceThrowsException() throws Exception {
        when(emitterService.addWebClientEmitter()).thenThrow(new RuntimeException("Emitter failure"));

        mockMvc.perform(get("/web"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testLogin_Success() throws Exception {
        String testUsername = "guest";
        String testPassword = "guest1";
        when(loginService.login(testUsername, testPassword)).thenReturn(true);

        mockMvc.perform(post("/web/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCredentialsDTO(testUsername, testPassword))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testLogin_Failure() throws Exception {
        when(loginService.login("user", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/web/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCredentialsDTO("user", "wrongpassword"))))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("false"));
    }


    @Test
    public void login_NullRequestBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/web/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    @Test
    public void login_ValidRequestResponseDetails() throws Exception {
        String username = "admin";
        String password = "adminPass";
        when(loginService.login(username, password)).thenReturn(true);

        MockHttpServletResponse response = mockMvc.perform(post("/web/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse();


    }
}