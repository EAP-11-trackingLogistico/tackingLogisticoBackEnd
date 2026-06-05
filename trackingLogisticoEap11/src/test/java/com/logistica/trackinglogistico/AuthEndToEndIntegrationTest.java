package com.logistica.trackinglogistico;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthEndToEndIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void fullAuthFlowShouldRegisterLoginAndAccessProtectedEndpoint() {
        String usuario = "testuser" + System.currentTimeMillis();

        String registerBody = String.format("""
                {
                    "nombre": "Test User",
                    "usuario": "%s",
                    "contrasena": "password123",
                    "rol": "OPERATOR"
                }
                """, usuario);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                baseUrl() + "/api/auth/register",
                createJsonRequest(registerBody),
                String.class
        );
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertTrue(registerResponse.getBody().contains("token"));

        String loginBody = String.format("""
                {
                    "usuario": "%s",
                    "contrasena": "password123"
                }
                """, usuario);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl() + "/api/auth/login",
                createJsonRequest(loginBody),
                String.class
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertTrue(loginResponse.getBody().contains("token"));

        String token = loginResponse.getBody()
                .replaceAll(".*\"token\":\"", "")
                .replaceAll("\".*", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> protectedRequest = new HttpEntity<>(headers);

        ResponseEntity<String> pingResponse = restTemplate.exchange(
                baseUrl() + "/api/shipments/ping",
                HttpMethod.GET, protectedRequest, String.class
        );
        assertEquals(HttpStatus.OK, pingResponse.getStatusCode());
    }

    @Test
    void unauthenticatedRequestShouldReturn403() {
        try {
            restTemplate.getForEntity(
                    baseUrl() + "/api/reports/transit-times?startDate=2026-01-01T00:00:00&endDate=2026-01-02T00:00:00",
                    String.class
            );
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("401") || e.getMessage().contains("403"));
        }
    }

    @Test
    void loginWithInvalidCredentialsShouldReturn400() {
        String body = """
                {
                    "usuario": "nonexistent",
                    "contrasena": "wrong"
                }
                """;

        try {
            restTemplate.postForEntity(
                    baseUrl() + "/api/auth/login",
                    createJsonRequest(body),
                    String.class
            );
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("401"));
        }
    }

    private HttpEntity<String> createJsonRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
