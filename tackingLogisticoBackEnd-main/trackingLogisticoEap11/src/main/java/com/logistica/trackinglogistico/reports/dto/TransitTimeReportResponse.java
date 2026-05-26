package com.logistica.trackinglogistico.reports.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TransitTimeReportResponse(
        LocalDateTime startDate,
        LocalDateTime endDate,
        long totalDeliveredShipments,
        double averageTransitHours,
        List<TransitTimeReportItem> shipments
) {
    public record TransitTimeReportItem(
            Integer shipmentId,
            String trackingNumber,
            LocalDateTime registrationDate,
            LocalDateTime deliveryDate,
            double transitHours
    ) {
    }
}