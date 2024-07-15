package com.example.loginapp.controllers;

import com.example.loginapp.models.ConnectionStatusToWebClientDTO;

import com.example.loginapp.models.UserCredentialsDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/web")
public class WebController {
    private final LoginService loginService;
    private final EmitterService emitterService;

    @GetMapping
    public SseEmitter streamWebNumbers() {
        try {
            return emitterService.addWebClientEmitter1();
        } catch (Exception e) {
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ConnectionStatusToWebClientDTO> getConnectionStatus() {
        ConnectionStatusToWebClientDTO defaultWebClientDTO = new ConnectionStatusToWebClientDTO(false, false);
        return ResponseEntity.ok().body(defaultWebClientDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody UserCredentialsDTO credentials) {
        boolean loginSuccess = loginService.login(credentials.getUsername(), credentials.getPassword());
        if (loginSuccess) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }
}
