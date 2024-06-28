package com.example.loginapp.controller;

import com.example.loginapp.model.FromProducerDTO;
import com.example.loginapp.model.FromConsumerDTO;
import com.example.loginapp.model.ToConsumerDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.NumberService;
import com.example.loginapp.services.OrganSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NumberController {


    private final EmitterService emitterService;
    private final NumberService numberService;
    private final OrganSettingsService organSettingsService;

    @PostMapping("/gestures")
    public ResponseEntity<String> handleNumber(@RequestBody FromProducerDTO body) {
        try {
            numberService.sendNumber(body.number());
            return ResponseEntity.ok("Processed number: " + body.number());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing number: " + e.getMessage());
        }
    }

    @GetMapping("/special")
    public SseEmitter streamSpecial() {
        try {
            return emitterService.addConsumerEmitter();
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }

    @GetMapping("/numbers")
    public SseEmitter streamWebNumbers() {
        try {
            return emitterService.addWebClientEmitter();
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }

    @PostMapping("/special/config")
    public ResponseEntity<ToConsumerDTO> configureMaxKeyboards(@RequestBody FromConsumerDTO config) {
        System.out.println("MAX= " + config.getKeyboardsMax() + " DEFAULT= " + config.getDefaultKeyboards());
        try {
            return numberService.getResponse(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ToConsumerDTO(-1, "Error: " + e.getMessage(),-1));
        }
    }



    @GetMapping("/settings-stream")
    public SseEmitter streamSettings() {
        return emitterService.addSettingsEmitter();
    }
}
