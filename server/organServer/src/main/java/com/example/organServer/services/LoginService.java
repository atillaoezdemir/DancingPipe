package com.example.organServer.services;

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

//The LoginService is responsible for managing user authentication in the frontend.
@Service
public class LoginService {
    @Getter
    private final Map<String, String> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    //not the best way to store credentials, but it was bonus task.
    private static final String CREDENTIALS_FILE = "credentials.txt";

    //	Annotation PostConstruct ensures that the init() method is executed after the bean is constructed and dependencies are injected.
    // init() initializes the service by loading credentials from a file
    @PostConstruct
    public void init() {
        try {
            loadCredentialsFromFile();
        } catch (IOException e) {
            System.out.println("Failed to load credentials from file: " + e.getMessage());
        }
    }

    // Loads user credentials from credentials.txt, encodes passwords, and stores them in a map.
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

    // Verifies the provided username and password.
    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return users.containsKey(username) && passwordEncoder.matches(password, users.get(username));
    }

}
