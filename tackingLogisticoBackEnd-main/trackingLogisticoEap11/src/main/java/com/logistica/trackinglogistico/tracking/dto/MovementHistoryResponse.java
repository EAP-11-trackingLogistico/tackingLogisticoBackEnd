package com.logistica.trackinglogistico.tracking.dto;

import java.util.List;

public class MovementHistoryResponse {

    private String trackingId;
    private String currentStatus;
    private int totalEvents;
    private String message;
    private List<MovementEventItem> events;

    public MovementHistoryResponse(
            String trackingId,
            String currentStatus,
            int totalEvents,
            String message,
            List<MovementEventItem> events
    ) {
        this.trackingId = trackingId;
        this.currentStatus = currentStatus;
        this.totalEvents = totalEvents;
        this.message = message;
        this.events = events;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public int getTotalEvents() {
        return totalEvents;
    }

    public String getMessage() {
        return message;
    }

    public List<MovementEventItem> getEvents() {
        return events;
    }
}
