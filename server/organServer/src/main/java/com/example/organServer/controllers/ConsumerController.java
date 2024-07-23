package com.example.organServer.controllers;

import com.example.organServer.models.FromConsumerDTO;
import com.example.organServer.models.ToConsumerDTO;
import com.example.organServer.services.EmitterService;
import com.example.organServer.services.NumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.validation.Valid;

//This controller handles incoming HTTP requests related to "Organ Sequencer" component.
@RequiredArgsConstructor
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final EmitterService emitterService;
    private final NumberService numberService;

//This HTTP method is used to send commands to be executed in the "Organ Sequencer" component.
//Consumer() attempts to create a new SseEmitter object via the EmitterService and returns it in the response.
    @GetMapping
    public ResponseEntity<SseEmitter> consumer() {
        try {
            SseEmitter emitter = emitterService.addConsumerEmitter();
            return ResponseEntity.ok(emitter);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
//This HTTP method is used to receive and adjust the configuration settings.
    @PostMapping
    public ResponseEntity<ToConsumerDTO> adjustConfiguration(@RequestBody @Valid FromConsumerDTO config) {
        try {
            return numberService.getResponse(config);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}