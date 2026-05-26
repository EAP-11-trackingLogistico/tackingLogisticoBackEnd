package com.logistica.trackinglogistico.reports.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ShipmentVolumeReportResponse(
        LocalDateTime startDate,
        LocalDateTime endDate,
        long totalShipments,
        List<ShipmentVolumeItem> volumeByDate
) {
    public record ShipmentVolumeItem(
            LocalDate period,
            long totalShipments
    ) {
    }
}
