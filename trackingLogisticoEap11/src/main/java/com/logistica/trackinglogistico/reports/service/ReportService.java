package com.logistica.trackinglogistico.reports.service;

import com.logistica.trackinglogistico.reports.dto.DelayReportResponse;
import com.logistica.trackinglogistico.reports.dto.ShipmentVolumeReportResponse;
import com.logistica.trackinglogistico.reports.dto.TransitTimeReportResponse;
import com.logistica.trackinglogistico.reports.repository.ReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public TransitTimeReportResponse getTransitTimeReport(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        validateDateRange(startDate, endDate);

        List<TransitTimeReportResponse.TransitTimeReportItem> shipments =
                reportRepository.findTransitTimes(startDate, endDate);

        double averageTransitHours = shipments.stream()
                .mapToDouble(TransitTimeReportResponse.TransitTimeReportItem::transitHours)
                .average()
                .orElse(0.0);

        return new TransitTimeReportResponse(
                startDate,
                endDate,
                shipments.size(),
                averageTransitHours,
                shipments
        );
    }

    public DelayReportResponse getDelayReport(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        validateDateRange(startDate, endDate);

        List<DelayReportResponse.DelayReportItem> delayedShipments =
                reportRepository.findDelayedShipments(startDate, endDate);

        return new DelayReportResponse(
                startDate,
                endDate,
                delayedShipments.size(),
                delayedShipments
        );
    }

    public ShipmentVolumeReportResponse getShipmentVolumeReport(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        validateDateRange(startDate, endDate);

        List<ShipmentVolumeReportResponse.ShipmentVolumeItem> volumeByDate =
                reportRepository.findShipmentVolume(startDate, endDate);

        long totalShipments = volumeByDate.stream()
                .mapToLong(ShipmentVolumeReportResponse.ShipmentVolumeItem::totalShipments)
                .sum();

        return new ShipmentVolumeReportResponse(
                startDate,
                endDate,
                totalShipments,
                volumeByDate
        );
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y fin son obligatorias"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
            );
        }
    }
}
