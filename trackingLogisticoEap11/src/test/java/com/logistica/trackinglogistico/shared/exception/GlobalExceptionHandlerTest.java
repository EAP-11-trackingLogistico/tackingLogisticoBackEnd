package com.logistica.trackinglogistico.shared.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("No encontrado");

        ResponseEntity<Map<String, Object>> result = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(404, result.getBody().get("status"));
        assertEquals("No encontrado", result.getBody().get("error"));
    }

    @Test
    void handleBadRequestShouldReturn400() {
        BadRequestException ex = new BadRequestException("Solicitud inválida");

        ResponseEntity<Map<String, Object>> result = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(400, result.getBody().get("status"));
        assertEquals("Solicitud inválida", result.getBody().get("error"));
    }

    @Test
    void handleAlreadyExistsShouldReturn409() {
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Ya existe");

        ResponseEntity<Map<String, Object>> result = handler.handleAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals(409, result.getBody().get("status"));
        assertEquals("Ya existe", result.getBody().get("error"));
    }

    @Test
    void handleMissingParamShouldReturn400() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("startDate", "String");

        ResponseEntity<Map<String, Object>> result = handler.handleMissingParam(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Parámetro requerido: startDate", result.getBody().get("error"));
    }

    @Test
    void handleTypeMismatchShouldReturn400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("startDate");
        when(ex.getRequiredType()).thenReturn((Class) java.time.LocalDateTime.class);

        ResponseEntity<Map<String, Object>> result = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        String error = (String) result.getBody().get("error");
        assertNotNull(error);
        assertEquals("Formato inválido para 'startDate'. Se esperaba LocalDateTime", error);
    }

    @Test
    void handleGeneralShouldReturn500() {
        Exception ex = new RuntimeException("Error interno");

        ResponseEntity<Map<String, Object>> result = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(500, result.getBody().get("status"));
        assertEquals("Error interno del servidor", result.getBody().get("error"));
    }

    @Test
    void handleResponseStatusExceptionShouldReturnStatusCode() {
        org.springframework.web.server.ResponseStatusException ex =
                new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Fecha inválida");

        ResponseEntity<Map<String, Object>> result = handler.handleResponseStatusException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
