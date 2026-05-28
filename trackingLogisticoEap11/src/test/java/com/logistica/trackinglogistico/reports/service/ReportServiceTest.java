package com.logistica.trackinglogistico.reports.service;

import com.logistica.trackinglogistico.reports.dto.DelayReportResponse;
import com.logistica.trackinglogistico.reports.dto.ShipmentVolumeReportResponse;
import com.logistica.trackinglogistico.reports.dto.TransitTimeReportResponse;
import com.logistica.trackinglogistico.reports.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.of(2026, 5, 1, 0, 0, 0);
        endDate = LocalDateTime.of(2026, 5, 31, 23, 59, 59);
    }

    @Test
    void getTransitTimeReportShouldReturnAverageAndShipments() {
        TransitTimeReportResponse.TransitTimeReportItem item1 =
                new TransitTimeReportResponse.TransitTimeReportItem(
                        1, "111111", startDate.plusDays(1), startDate.plusDays(3), 48.0
                );
        TransitTimeReportResponse.TransitTimeReportItem item2 =
                new TransitTimeReportResponse.TransitTimeReportItem(
                        2, "222222", startDate.plusDays(2), startDate.plusDays(4), 48.0
                );
        List<TransitTimeReportResponse.TransitTimeReportItem> items = List.of(item1, item2);

        when(reportRepository.findTransitTimes(startDate, endDate)).thenReturn(items);

        TransitTimeReportResponse result = reportService.getTransitTimeReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
        assertEquals(2, result.totalDeliveredShipments());
        assertEquals(48.0, result.averageTransitHours());
        assertEquals(2, result.shipments().size());
        assertEquals("111111", result.shipments().get(0).trackingNumber());
    }

    @Test
    void getTransitTimeReportShouldReturnZeroWhenNoShipments() {
        when(reportRepository.findTransitTimes(startDate, endDate)).thenReturn(Collections.emptyList());

        TransitTimeReportResponse result = reportService.getTransitTimeReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(0, result.totalDeliveredShipments());
        assertEquals(0.0, result.averageTransitHours());
        assertTrue(result.shipments().isEmpty());
    }

    @Test
    void getTransitTimeReportShouldThrowWhenStartAfterEnd() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getTransitTimeReport(endDate, startDate));
    }

    @Test
    void getTransitTimeReportShouldThrowWhenStartNull() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getTransitTimeReport(null, endDate));
    }

    @Test
    void getTransitTimeReportShouldThrowWhenEndNull() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getTransitTimeReport(startDate, null));
    }

    @Test
    void getDelayReportShouldReturnDelayedShipments() {
        DelayReportResponse.DelayReportItem item = new DelayReportResponse.DelayReportItem(
                1, "111111", "DELAYED", "Bodega Central",
                startDate.plusDays(5), "Juan"
        );
        List<DelayReportResponse.DelayReportItem> items = List.of(item);

        when(reportRepository.findDelayedShipments(startDate, endDate)).thenReturn(items);

        DelayReportResponse result = reportService.getDelayReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
        assertEquals(1, result.totalDelayedShipments());
        assertEquals(1, result.delayedShipments().size());
        assertEquals("111111", result.delayedShipments().get(0).trackingNumber());
    }

    @Test
    void getDelayReportShouldReturnZeroWhenNoDelays() {
        when(reportRepository.findDelayedShipments(startDate, endDate))
                .thenReturn(Collections.emptyList());

        DelayReportResponse result = reportService.getDelayReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(0, result.totalDelayedShipments());
        assertTrue(result.delayedShipments().isEmpty());
    }

    @Test
    void getDelayReportShouldThrowWhenStartAfterEnd() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getDelayReport(endDate, startDate));
    }

    @Test
    void getDelayReportShouldThrowWhenDatesNull() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getDelayReport(null, endDate));

        assertThrows(ResponseStatusException.class,
                () -> reportService.getDelayReport(startDate, null));
    }

    @Test
    void getShipmentVolumeReportShouldReturnVolumeByDate() {
        ShipmentVolumeReportResponse.ShipmentVolumeItem item1 =
                new ShipmentVolumeReportResponse.ShipmentVolumeItem(
                        LocalDate.of(2026, 5, 10), 5
                );
        ShipmentVolumeReportResponse.ShipmentVolumeItem item2 =
                new ShipmentVolumeReportResponse.ShipmentVolumeItem(
                        LocalDate.of(2026, 5, 11), 3
                );
        List<ShipmentVolumeReportResponse.ShipmentVolumeItem> items = List.of(item1, item2);

        when(reportRepository.findShipmentVolume(startDate, endDate)).thenReturn(items);

        ShipmentVolumeReportResponse result = reportService.getShipmentVolumeReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(startDate, result.startDate());
        assertEquals(endDate, result.endDate());
        assertEquals(8, result.totalShipments());
        assertEquals(2, result.volumeByDate().size());
        assertEquals(LocalDate.of(2026, 5, 10), result.volumeByDate().get(0).period());
        assertEquals(5, result.volumeByDate().get(0).totalShipments());
    }

    @Test
    void getShipmentVolumeReportShouldReturnZeroWhenNoShipments() {
        when(reportRepository.findShipmentVolume(startDate, endDate))
                .thenReturn(Collections.emptyList());

        ShipmentVolumeReportResponse result = reportService.getShipmentVolumeReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(0, result.totalShipments());
        assertTrue(result.volumeByDate().isEmpty());
    }

    @Test
    void getShipmentVolumeReportShouldThrowWhenStartAfterEnd() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getShipmentVolumeReport(endDate, startDate));
    }

    @Test
    void getShipmentVolumeReportShouldThrowWhenDatesNull() {
        assertThrows(ResponseStatusException.class,
                () -> reportService.getShipmentVolumeReport(null, endDate));

        assertThrows(ResponseStatusException.class,
                () -> reportService.getShipmentVolumeReport(startDate, null));
    }
}
