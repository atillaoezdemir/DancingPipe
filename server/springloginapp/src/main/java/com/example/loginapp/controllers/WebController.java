package com.example.loginapp.controllers;


import com.example.loginapp.models.UserCredentialsDTO;
import com.example.loginapp.services.EmitterService;
import com.example.loginapp.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// This controller handles incoming HTTP requests related to frontend.
@RequiredArgsConstructor
@RestController
@RequestMapping("/web")
public class WebController {
    private final LoginService loginService;
    private final EmitterService emitterService;

    //This HTTP method is used to send actual information to the frontend.
    //streamWebNumbers() attempts to create a new SseEmitter object via the EmitterService and returns it in the response.
    @GetMapping
    public ResponseEntity<SseEmitter> streamWebNumbers() {
        try {
            SseEmitter emitter = emitterService.addWebClientEmitter();
            return ResponseEntity.ok(emitter);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    //This HTTP method is used to check user credentials.
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
