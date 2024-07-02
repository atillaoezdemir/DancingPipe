package com.example.loginapp.controller;

import com.example.loginapp.model.ConnectionStatusToWebClientDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/web")
public class WebController {

    @GetMapping("/status")
    public ResponseEntity<ConnectionStatusToWebClientDTO> getConnectionStatus() {
        ConnectionStatusToWebClientDTO defaultWebClientDTO = new ConnectionStatusToWebClientDTO(false, false);
        return ResponseEntity.ok().body(defaultWebClientDTO);
    }
}
