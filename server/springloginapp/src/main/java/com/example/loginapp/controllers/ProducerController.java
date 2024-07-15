package com.example.loginapp.controllers;

import com.example.loginapp.models.FromProducerDTO;
import com.example.loginapp.services.NumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/producer")
public class ProducerController {


    private final NumberService numberService;

    @PostMapping
    public ResponseEntity<String> handleNumber(@RequestBody FromProducerDTO body) {
        try {
            numberService.sendNumber(body.number());
            return ResponseEntity.ok("Processed number: " + body.number());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing number: " + e.getMessage());
        }
    }


}
