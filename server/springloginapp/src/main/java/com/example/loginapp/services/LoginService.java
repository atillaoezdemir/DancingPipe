package com.example.loginapp.services;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    @Getter
    private final Map<String, String> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String CREDENTIALS_FILE = "credentials.txt";

    @PostConstruct
    public void init() {
        try {
            loadCredentialsFromFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load credentials from file", e);
        }
    }

    private void loadCredentialsFromFile() throws IOException {
        ClassPathResource resource = new ClassPathResource(CREDENTIALS_FILE);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String fileContent = FileCopyUtils.copyToString(reader);
            Arrays.stream(fileContent.split("\\r?\\n"))
                    .map(line -> line.split("="))
                    .forEach(parts -> {
                        if (parts.length == 2) {
                            String username = parts[0].trim();
                            String password = parts[1].trim();
                            users.put(username, passwordEncoder.encode(password));
                        }
                    });
        }
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return users.containsKey(username) && passwordEncoder.matches(password, users.get(username));
    }

}
