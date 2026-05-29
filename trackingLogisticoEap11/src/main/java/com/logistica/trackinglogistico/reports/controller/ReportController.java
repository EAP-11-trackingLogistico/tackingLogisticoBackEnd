package com.logistica.trackinglogistico.reports.controller;

import com.logistica.trackinglogistico.reports.dto.DelayReportResponse;
import com.logistica.trackinglogistico.reports.dto.ShipmentVolumeReportResponse;
import com.logistica.trackinglogistico.reports.dto.TransitTimeReportResponse;
import com.logistica.trackinglogistico.reports.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/transit-times")
    public EntityModel<TransitTimeReportResponse> getTransitTimeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {
        TransitTimeReportResponse response = reportService.getTransitTimeReport(startDate, endDate);

        return EntityModel.of(response,
                linkTo(methodOn(ReportController.class)
                        .getTransitTimeReport(startDate, endDate)).withSelfRel());
    }

    @GetMapping("/delays")
    public EntityModel<DelayReportResponse> getDelayReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {
        DelayReportResponse response = reportService.getDelayReport(startDate, endDate);

        return EntityModel.of(response,
                linkTo(methodOn(ReportController.class)
                        .getDelayReport(startDate, endDate)).withSelfRel());
    }

    @GetMapping("/shipment-volume")
    public EntityModel<ShipmentVolumeReportResponse> getShipmentVolumeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {
        ShipmentVolumeReportResponse response =
                reportService.getShipmentVolumeReport(startDate, endDate);

        return EntityModel.of(response,
                linkTo(methodOn(ReportController.class)
                        .getShipmentVolumeReport(startDate, endDate)).withSelfRel());
    }
}
