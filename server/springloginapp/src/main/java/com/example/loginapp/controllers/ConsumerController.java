package com.example.loginapp.controllers;

import com.example.loginapp.models.FromConsumerDTO;
import com.example.loginapp.models.ToConsumerDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.NumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final EmitterService emitterService;
    private final NumberService numberService;

    @GetMapping
    public ResponseEntity<SseEmitter> consumer() {
        try {
            SseEmitter emitter = emitterService.addConsumerEmitter();
            return ResponseEntity.ok(emitter);
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emitter);
        }
    }

    @PostMapping
    public ResponseEntity<ToConsumerDTO> configureMaxKeyboards(@RequestBody @Valid FromConsumerDTO config) {
        try {
            return numberService.getResponse(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ToConsumerDTO(-1,
                    "Error: " + e.getMessage(), -1));
        }
    }
}