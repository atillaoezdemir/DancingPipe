package com.example.loginapp.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
@Service
public class LoginService {
    private final Map<String, String> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        loadCredentialsFromFile();
    }

    private void loadCredentialsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource("credentials.txt");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String fileContent = FileCopyUtils.copyToString(reader);

            String[] lines = fileContent.split("\\r?\\n");

            for (String line : lines) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    users.put(username, passwordEncoder.encode(password));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load credentials from file", e);
        }
    }

    public boolean login(String username, String password) {
        String storedPassword = users.get(username);
        return storedPassword != null && passwordEncoder.matches(password, storedPassword);
    }
}