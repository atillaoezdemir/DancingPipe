package com.example.loginapp.controllers;

import com.example.loginapp.models.FromConsumerDTO;
import com.example.loginapp.models.ToConsumerDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.NumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final EmitterService emitterService;
    private final NumberService numberService;

    @GetMapping
    public SseEmitter streamSpecial() {
        try {
            return emitterService.addConsumerEmitter();
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }

    @PostMapping
    public ResponseEntity<ToConsumerDTO> configureMaxKeyboards(@RequestBody FromConsumerDTO config) {
        try {
            return numberService.getResponse(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ToConsumerDTO(-1, "Error: " + e.getMessage(), -1));
        }
    }
}