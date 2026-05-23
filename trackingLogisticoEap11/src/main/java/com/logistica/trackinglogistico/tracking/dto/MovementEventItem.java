package com.logistica.trackinglogistico.tracking.dto;

import java.time.LocalDateTime;

public class MovementEventItem {

    private Integer eventId;
    private String eventName;
    private String eventType;
    private String location;
    private LocalDateTime eventDate;
    private String operatorName;

    public MovementEventItem(
            Integer eventId,
            String eventName,
            String eventType,
            String location,
            LocalDateTime eventDate,
            String operatorName
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.location = location;
        this.eventDate = eventDate;
        this.operatorName = operatorName;
    }

    public Integer getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getOperatorName() {
        return operatorName;
    }
}
