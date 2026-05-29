package com.logistica.trackinglogistico.tracking.controller;

import com.logistica.trackinglogistico.shared.exception.GlobalExceptionHandler;
import com.logistica.trackinglogistico.tracking.dto.CreateLogisticEventRequest;
import com.logistica.trackinglogistico.tracking.dto.LogisticEventResponse;
import com.logistica.trackinglogistico.tracking.service.LogisticEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogisticEventControllerIntegrationTest {

    private MockMvc mockMvc;
    private LogisticEventService logisticEventService;

    @BeforeEach
    void setUp() {
        logisticEventService = mock(LogisticEventService.class);
        LogisticEventController controller = new LogisticEventController(logisticEventService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createEventShouldReturn201() throws Exception {
        LogisticEventResponse response = new LogisticEventResponse(
                1, "123456", 1, "Bogotá",
                LocalDateTime.of(2026, 5, 23, 10, 30), "REGISTERED"
        );

        when(logisticEventService.createEvent(any(CreateLogisticEventRequest.class)))
                .thenReturn(response);

        String body = """
                {
                    "trackingId": "123456",
                    "operatorId": 1,
                    "nombre": "Evento de prueba",
                    "location": "Bogotá",
                    "eventDate": "2026-05-23T10:30:00",
                    "eventType": "REGISTERED"
                }
                """;

        mockMvc.perform(post("/api/logistic-events")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.trackingId").value("123456"))
                .andExpect(jsonPath("$.location").value("Bogotá"));
    }

    @Test
    void createEventWithMissingFieldsShouldReturn400() throws Exception {
        String body = """
                {
                    "trackingId": "123456"
                }
                """;

        mockMvc.perform(post("/api/logistic-events")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
