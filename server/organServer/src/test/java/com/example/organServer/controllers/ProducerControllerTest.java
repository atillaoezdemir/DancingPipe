//package com.example.organServer.controllers;
//
//import com.example.organServer.models.FromProducerDTO;
//import com.example.organServer.services.NumberService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ProducerController.class)
//public class ProducerControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private NumberService numberService;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    public void handleNumber_ShouldReturnProcessed_WhenValidRequest() throws Exception {
//        FromProducerDTO dto = new FromProducerDTO(123);
//        String jsonDto = objectMapper.writeValueAsString(dto);
//
//        doNothing().when(numberService).sendNumber(anyInt());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/producer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonDto))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Processed number: 123"));
//
//        verify(numberService).sendNumber(123);
//    }
//
//    @Test
//    public void handleNumber_NullRequestBody_ShouldReturnBadRequest() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/producer")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void handleNumber_InvalidDataFormat_ShouldReturnBadRequest() throws Exception {
//        String invalidJson = "{ \"invalid\": \"data\" }";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/producer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(invalidJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void handleNumber_NonIntegerNumber_ShouldReturnBadRequest() throws Exception {
//        String nonIntegerJson = "{\"number\": \"not_an_integer\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/producer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(nonIntegerJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void handleNumber_ServiceThrowsIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
//        FromProducerDTO dto = new FromProducerDTO(456);
//        String jsonDto = objectMapper.writeValueAsString(dto);
//
//        doThrow(new IllegalArgumentException("Invalid argument")).when(numberService).sendNumber(anyInt());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/producer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonDto))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Error processing number: Invalid argument"));
//    }
//}
