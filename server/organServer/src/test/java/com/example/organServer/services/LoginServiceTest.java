package com.example.organServer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService.init();
    }

    @Test
    void testValidLogin() {
        assertTrue(loginService.login("admin", "password1"));
        assertTrue(loginService.login("guest", "guest1"));
    }

    @Test
    void testInvalidLogin() {
        assertFalse(loginService.login("admin", "wrongpassword"));
        assertFalse(loginService.login("nonexistentuser", "password"));
    }

    @Test
    void testNonexistentUser() {
        assertFalse(loginService.login("nonexistentuser", "password"));
    }

    @Test
    void testEmptyUsername() {
        assertFalse(loginService.login("", "password1"));
    }

    @Test
    void testEmptyPassword() {
        assertFalse(loginService.login("admin", ""));
    }

    @Test
    void testIncorrectPassword() {
        assertFalse(loginService.login("admin", "incorrectpassword"));
    }

    @Test
    void testEmptyUsernameAndPassword() {
        assertFalse(loginService.login("", ""));
    }

    @Test
    void testNullUsername() {
        assertFalse(loginService.login(null, "password1"));
    }

    @Test
    void testNullPassword() {
        assertFalse(loginService.login("admin", null));
    }

    @Test
    void testNullUsernameAndPassword() {
        assertFalse(loginService.login(null, null));
    }

}
