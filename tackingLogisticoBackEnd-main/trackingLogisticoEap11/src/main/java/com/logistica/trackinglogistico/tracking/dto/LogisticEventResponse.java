package com.logistica.trackinglogistico.tracking.dto;

import java.time.LocalDateTime;

public class LogisticEventResponse {

    private Integer id;
    private String trackingId;
    private Integer operatorId;
    private String location;
    private LocalDateTime eventDate;
    private String eventType;

    public LogisticEventResponse(Integer id, String trackingId, Integer operatorId, String location, LocalDateTime eventDate, String eventType) {
        this.id = id;
        this.trackingId = trackingId;
        this.operatorId = operatorId;
        this.location = location;
        this.eventDate = eventDate;
        this.eventType = eventType;
    }

    public Integer getId() {
        return id;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getEventType() {
        return eventType;
    }
}