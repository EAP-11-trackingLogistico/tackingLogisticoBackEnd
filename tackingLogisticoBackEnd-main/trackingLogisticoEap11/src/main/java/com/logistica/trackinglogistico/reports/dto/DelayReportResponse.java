package com.logistica.trackinglogistico.reports.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DelayReportResponse(
        LocalDateTime startDate,
        LocalDateTime endDate,
        long totalDelayedShipments,
        List<DelayReportItem> delayedShipments
) {
    public record DelayReportItem(
            Integer shipmentId,
            String trackingNumber,
            String currentStatus,
            String delayLocation,
            LocalDateTime delayDate,
            String operatorName
    ) {
    }
}