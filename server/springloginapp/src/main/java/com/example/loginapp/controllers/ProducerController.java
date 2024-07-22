package com.example.loginapp.controllers;

import com.example.loginapp.models.FromProducerDTO;
import com.example.loginapp.services.NumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

//This controller handles incoming HTTP requests related to the "Camera" component.
@RequiredArgsConstructor
@RestController
@RequestMapping("/producer")
public class ProducerController {


    private final NumberService numberService;

    //This  HTTP method is used to receive commands from the "Camera" component and process them using the sendNumber() method from NumberService.
    @PostMapping
    public ResponseEntity<String> handleNumber(@RequestBody @Valid FromProducerDTO body) {
        try {
            numberService.sendNumber(body.number());
            return ResponseEntity.ok("Processed number: " + body.number());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing number: " + e.getMessage());
        }
    }
}
