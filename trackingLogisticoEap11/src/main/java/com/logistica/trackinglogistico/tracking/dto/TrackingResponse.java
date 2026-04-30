package com.logistica.trackinglogistico.tracking.dto;

import java.time.LocalDateTime;

public class TrackingResponse {

    private Integer trackingId;
    private String currentStatus;
    private String lastLocation;
    private LocalDateTime lastEventDate;

    public TrackingResponse(Integer trackingId, String lastLocation, LocalDateTime lastEventDate) {
        this.trackingId = trackingId;
        this.lastLocation = lastLocation;
        this.lastEventDate = lastEventDate;
    }

    public Integer getTrackingId() {
        return trackingId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public LocalDateTime getLastEventDate() {
        return lastEventDate;
    }
}