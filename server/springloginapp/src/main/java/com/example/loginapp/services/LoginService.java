package com.example.loginapp.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoginService {
    private final List<String> loggedInUsers = new ArrayList<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, String> users = Map.of(
            "admin", passwordEncoder.encode("password1"),
            "guest", passwordEncoder.encode("guest1")
    );

    public boolean login(String username, String password) {
        String storedPassword = users.get(username);
        if (users.containsKey(username) && passwordEncoder.matches(password, storedPassword)) {
            if (!loggedInUsers.contains(username)) {
                loggedInUsers.add(username);
            }
            return true;
        }
        return false;
    }

    public boolean isLoggedIn(String username) {
        return loggedInUsers.contains(username);
    }
}
