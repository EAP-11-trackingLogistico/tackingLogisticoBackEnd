package com.logistica.trackinglogistico.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tdGVzdGluZy1wdXJwb3Nlcy1vbmx5",
                3600000L
        );
    }

    @Test
    void generateTokenShouldReturnNonEmptyString() {
        String token = jwtService.generateToken("juan123", "OPERATOR");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsernameShouldReturnCorrectValue() {
        String token = jwtService.generateToken("juan123", "OPERATOR");

        assertEquals("juan123", jwtService.extractUsername(token));
    }

    @Test
    void extractRoleShouldReturnCorrectValue() {
        String token = jwtService.generateToken("juan123", "ADMIN");

        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    void validTokenShouldPassValidation() {
        String token = jwtService.generateToken("juan123", "OPERATOR");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void invalidTokenShouldFailValidation() {
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid(null));
    }
}
