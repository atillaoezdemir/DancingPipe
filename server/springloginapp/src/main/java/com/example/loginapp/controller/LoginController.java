package com.example.loginapp.controller;

import com.example.loginapp.services.LoginService;
import com.example.loginapp.model.UserCredentialsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody UserCredentialsDTO credentials) {
        boolean loginSuccess = loginService.login(credentials.getUsername(), credentials.getPassword());
        if (loginSuccess) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @GetMapping("is-logged-in")
    public boolean isLoggedIn(String username) {
        return loginService.isLoggedIn(username);
    }
}
