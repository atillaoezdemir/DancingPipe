package com.example.organServer.services;

import com.example.organServer.models.DTOWrapper;
import com.example.organServer.models.ToConsumerDTO;
import com.example.organServer.models.ToWebClientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NumberServiceTest {

    @Mock
    private EmitterService emitterService;

    @Mock
    private OrganSettingsService organSettingsService;

    @InjectMocks
    private NumberService numberService;



    @Test
    void shouldHandleUndefinedAction() {
        // No stubbing needed here

        numberService.sendNumber(999);

        // Verifying that no actions are taken on undefined commands
        verify(organSettingsService, never()).sendStartCommand();
        verify(emitterService, never()).sendToConsumer(any(ToConsumerDTO.class));
        verify(emitterService, never()).sendToWebClient(any(ToWebClientDTO.class));
    }



}
