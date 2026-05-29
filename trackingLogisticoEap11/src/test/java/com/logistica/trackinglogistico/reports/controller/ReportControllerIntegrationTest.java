package com.logistica.trackinglogistico.reports.controller;

import com.logistica.trackinglogistico.reports.dto.DelayReportResponse;
import com.logistica.trackinglogistico.reports.dto.ShipmentVolumeReportResponse;
import com.logistica.trackinglogistico.reports.dto.TransitTimeReportResponse;
import com.logistica.trackinglogistico.reports.service.ReportService;
import com.logistica.trackinglogistico.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerIntegrationTest {

    private MockMvc mockMvc;
    private ReportService reportService;
    private static final LocalDateTime START = LocalDateTime.of(2026, 5, 1, 0, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 5, 31, 23, 59, 59);

    @BeforeEach
    void setUp() {
        reportService = mock(ReportService.class);
        ReportController controller = new ReportController(reportService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTransitTimeReportShouldReturn200() throws Exception {
        TransitTimeReportResponse.TransitTimeReportItem item =
                new TransitTimeReportResponse.TransitTimeReportItem(
                        1, "111111", START.plusDays(1), START.plusDays(3), 48.0
                );
        TransitTimeReportResponse response = new TransitTimeReportResponse(
                START, END, 1, 48.0, List.of(item)
        );
        when(reportService.getTransitTimeReport(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/transit-times")
                        .param("startDate", "2026-05-01T00:00:00")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveredShipments").value(1))
                .andExpect(jsonPath("$.averageTransitHours").value(48.0))
                .andExpect(jsonPath("$.shipments", hasSize(1)))
                .andExpect(jsonPath("$.shipments[0].trackingNumber").value("111111"));
    }

    @Test
    void getTransitTimeReportWithNoShipmentsShouldReturn200() throws Exception {
        TransitTimeReportResponse response = new TransitTimeReportResponse(
                START, END, 0, 0.0, Collections.emptyList()
        );
        when(reportService.getTransitTimeReport(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/transit-times")
                        .param("startDate", "2026-05-01T00:00:00")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeliveredShipments").value(0))
                .andExpect(jsonPath("$.averageTransitHours").value(0.0))
                .andExpect(jsonPath("$.shipments", hasSize(0)));
    }

    @Test
    void getTransitTimeReportMissingParamShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/reports/transit-times")
                        .param("startDate", "2026-05-01T00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Parámetro requerido: endDate"));
    }

    @Test
    void getTransitTimeReportInvalidFormatShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/reports/transit-times")
                        .param("startDate", "not-a-date")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getTransitTimeReportStartAfterEndShouldReturn400() throws Exception {
        when(reportService.getTransitTimeReport(END, START))
                .thenThrow(new ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "La fecha de inicio no puede ser posterior a la fecha de fin"
                ));

        mockMvc.perform(get("/api/reports/transit-times")
                        .param("startDate", "2026-05-31T23:59:59")
                        .param("endDate", "2026-05-01T00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("La fecha de inicio no puede ser posterior a la fecha de fin"));
    }

    @Test
    void getDelayReportShouldReturn200() throws Exception {
        DelayReportResponse.DelayReportItem item = new DelayReportResponse.DelayReportItem(
                1, "111111", "DELAYED", "Bodega Central",
                START.plusDays(5), "Juan"
        );
        DelayReportResponse response = new DelayReportResponse(
                START, END, 1, List.of(item)
        );
        when(reportService.getDelayReport(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/delays")
                        .param("startDate", "2026-05-01T00:00:00")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDelayedShipments").value(1))
                .andExpect(jsonPath("$.delayedShipments", hasSize(1)))
                .andExpect(jsonPath("$.delayedShipments[0].trackingNumber").value("111111"));
    }

    @Test
    void getDelayReportWithNoDelaysShouldReturn200() throws Exception {
        DelayReportResponse response = new DelayReportResponse(
                START, END, 0, Collections.emptyList()
        );
        when(reportService.getDelayReport(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/delays")
                        .param("startDate", "2026-05-01T00:00:00")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDelayedShipments").value(0))
                .andExpect(jsonPath("$.delayedShipments", hasSize(0)));
    }

    @Test
    void getDelayReportMissingParamShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/reports/delays")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parámetro requerido: startDate"));
    }

    @Test
    void getShipmentVolumeReportShouldReturn200() throws Exception {
        ShipmentVolumeReportResponse.ShipmentVolumeItem item =
                new ShipmentVolumeReportResponse.ShipmentVolumeItem(
                        LocalDate.of(2026, 5, 10), 5
                );
        ShipmentVolumeReportResponse response = new ShipmentVolumeReportResponse(
                START, END, 5, List.of(item)
        );
        when(reportService.getShipmentVolumeReport(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/shipment-volume")
                        .param("startDate", "2026-05-01T00:00:00")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments").value(5))
                .andExpect(jsonPath("$.volumeByDate", hasSize(1)))
                .andExpect(jsonPath("$.volumeByDate[0].period").value("2026-05-10"));
    }

    @Test
    void getShipmentVolumeReportWithNoShipmentsShouldReturn200() throws Exception {
        ShipmentVolumeReportResponse response = new ShipmentVolumeReportResponse(
                START, END, 0, Collections.emptyList()
        );
        when(reportService.getShipmentVolumeReport(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/shipment-volume")
                        .param("startDate", "2026-05-01T00:00:00")
                        .param("endDate", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments").value(0))
                .andExpect(jsonPath("$.volumeByDate", hasSize(0)));
    }

    @Test
    void getShipmentVolumeReportMissingParamShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/reports/shipment-volume"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parámetro requerido: startDate"));
    }
}
