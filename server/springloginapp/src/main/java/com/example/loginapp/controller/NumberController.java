package com.example.loginapp.controller;

import com.example.loginapp.model.FromConsumerDTO;
import com.example.loginapp.model.FromProducerDTO;
import com.example.loginapp.model.ToConsumerDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.NumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NumberController {


    private final EmitterService emitterService;
    private final NumberService numberService;

    @PostMapping("/producer")
    public ResponseEntity<String> handleNumber(@RequestBody FromProducerDTO body) {
        try {
            numberService.sendNumber(body.number());
            return ResponseEntity.ok("Processed number: " + body.number());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing number: " + e.getMessage());
        }
    }

    @GetMapping("/consumer")
    public SseEmitter streamSpecial() {
        try {
            return emitterService.addConsumerEmitter();
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }

    @GetMapping("/web")
    public SseEmitter streamWebNumbers() {
        try {
            return emitterService.addWebClientEmitter1();
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }


    @PostMapping("/consumer")
    public ResponseEntity<ToConsumerDTO> configureMaxKeyboards(@RequestBody FromConsumerDTO config) {
        System.out.println("MAX= " + config.getKeyboardsMax() + " DEFAULT= " + config.getDefaultKeyboards());
        try {
            return numberService.getResponse(config);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ToConsumerDTO(-1, "Error: " + e.getMessage(), -1));
        }
    }

}
