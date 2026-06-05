package com.logistica.trackinglogistico.tracking.controller;

import com.logistica.trackinglogistico.shared.exception.BadRequestException;
import com.logistica.trackinglogistico.shared.exception.GlobalExceptionHandler;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.tracking.dto.MovementEventItem;
import com.logistica.trackinglogistico.tracking.dto.MovementHistoryResponse;
import com.logistica.trackinglogistico.tracking.dto.ShipmentResponse;
import com.logistica.trackinglogistico.tracking.dto.TrackingResponse;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import com.logistica.trackinglogistico.tracking.service.LogisticEventService;
import com.logistica.trackinglogistico.tracking.service.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShipmentControllerIntegrationTest {

    private MockMvc mockMvc;
    private ShipmentService shipmentService;
    private LogisticEventService logisticEventService;

    @BeforeEach
    void setUp() {
        shipmentService = mock(ShipmentService.class);
        logisticEventService = mock(LogisticEventService.class);
        ShipmentController controller = new ShipmentController(shipmentService, logisticEventService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getMovementHistoryWithEventsShouldReturn200() throws Exception {
        MovementEventItem item1 = new MovementEventItem(
                1, "Paquete registrado", "REGISTERED", "Bogotá",
                LocalDateTime.of(2026, 5, 23, 10, 30), "Juan"
        );
        MovementEventItem item2 = new MovementEventItem(
                2, "En tránsito", "IN_TRANSIT", "Medellín",
                LocalDateTime.of(2026, 5, 23, 14, 0), "María"
        );
        MovementHistoryResponse response = new MovementHistoryResponse(
                "123456", "IN_TRANSIT", 2,
                "Historial obtenido exitosamente", List.of(item1, item2)
        );
        when(logisticEventService.getMovementHistory("123456")).thenReturn(response);

        mockMvc.perform(get("/api/shipments/123456/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("123456"))
                .andExpect(jsonPath("$.currentStatus").value("IN_TRANSIT"))
                .andExpect(jsonPath("$.totalEvents").value(2))
                .andExpect(jsonPath("$.message").value("Historial obtenido exitosamente"))
                .andExpect(jsonPath("$.events", hasSize(2)))
                .andExpect(jsonPath("$.events[0].location").value("Bogotá"))
                .andExpect(jsonPath("$.events[0].eventType").value("REGISTERED"))
                .andExpect(jsonPath("$.events[1].location").value("Medellín"))
                .andExpect(jsonPath("$.events[1].eventType").value("IN_TRANSIT"));
    }

    @Test
    void getMovementHistoryNoEventsShouldReturn200WithEmptyList() throws Exception {
        MovementHistoryResponse response = new MovementHistoryResponse(
                "123456", "REGISTERED", 0,
                "No hay eventos registrados para este envío", Collections.emptyList()
        );
        when(logisticEventService.getMovementHistory("123456")).thenReturn(response);

        mockMvc.perform(get("/api/shipments/123456/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("123456"))
                .andExpect(jsonPath("$.totalEvents").value(0))
                .andExpect(jsonPath("$.message").value("No hay eventos registrados para este envío"))
                .andExpect(jsonPath("$.events", hasSize(0)));
    }

    @Test
    void getMovementHistoryShipmentNotFoundShouldReturn404() throws Exception {
        when(logisticEventService.getMovementHistory("999999"))
                .thenThrow(new ResourceNotFoundException("No existe envío con trackingId: 999999"));

        mockMvc.perform(get("/api/shipments/999999/history"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("No existe envío con trackingId: 999999"));
    }

    @Test
    void getMovementHistoryServiceValidationErrorShouldReturn400() throws Exception {
        when(logisticEventService.getMovementHistory("INVALID!!"))
                .thenThrow(new BadRequestException("El número de seguimiento no puede estar vacío"));

        mockMvc.perform(get("/api/shipments/INVALID!!/history"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("El número de seguimiento no puede estar vacío"));
    }

    @Test
    void pingShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/shipments/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("API funcionando"));
    }

    @Test
    void getAllShipmentsShouldReturn200() throws Exception {
        Shipment shipment = new Shipment();
        shipment.setTrackingId("111111");
        Page<Shipment> page = new PageImpl<>(List.of(shipment));
        when(shipmentService.getAllShipments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllShipmentsEmptyShouldReturn200() throws Exception {
        Page<Shipment> page = new PageImpl<>(Collections.emptyList());
        when(shipmentService.getAllShipments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk());
    }

    @Test
    void getShipmentByTrackingIdShouldReturn200() throws Exception {
        TrackingResponse response = new TrackingResponse(
                "123456", "REGISTERED", "Bogotá",
                LocalDateTime.of(2026, 5, 23, 10, 30)
        );
        when(logisticEventService.getTracking("123456")).thenReturn(response);

        mockMvc.perform(get("/api/shipments/123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("123456"))
                .andExpect(jsonPath("$.currentStatus").value("REGISTERED"));
    }

    @Test
    void getShipmentByTrackingIdNotFoundShouldReturn404() throws Exception {
        when(logisticEventService.getTracking("999999"))
                .thenThrow(new ResourceNotFoundException("No existe envío con trackingId: 999999"));

        mockMvc.perform(get("/api/shipments/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatusShouldReturn200() throws Exception {
        ShipmentResponse response = new ShipmentResponse();
        response.setTrackingId("123456");
        response.setMessage("Estado actualizado correctamente");
        when(shipmentService.updateStatus(any(String.class), any())).thenReturn(response);

        String body = "{\"status\": \"IN_TRANSIT\"}";

        mockMvc.perform(patch("/api/shipments/123456/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("123456"))
                .andExpect(jsonPath("$.message").value("Estado actualizado correctamente"));
    }

    @Test
    void updateStatusNotFoundShouldReturn404() throws Exception {
        when(shipmentService.updateStatus(any(String.class), any()))
                .thenThrow(new ResourceNotFoundException("No se encontró el envío con trackingId: 999999"));

        String body = "{\"status\": \"IN_TRANSIT\"}";

        mockMvc.perform(patch("/api/shipments/999999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }
}
