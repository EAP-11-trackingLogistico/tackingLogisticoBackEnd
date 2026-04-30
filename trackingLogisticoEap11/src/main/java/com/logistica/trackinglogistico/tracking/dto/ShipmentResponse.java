package com.logistica.trackinglogistico.tracking.dto;

import java.time.LocalDateTime;

public class ShipmentResponse {

    private Integer shipmentId;
    private Integer trackingId;
    private Integer operatorId;
    private Integer packageId;
    private LocalDateTime createdAt;
    private String message;

    public ShipmentResponse() {
    }

    public ShipmentResponse(
            Integer shipmentId,
            Integer trackingId,
            Integer operatorId,
            Integer packageId,
            LocalDateTime createdAt,
            String message
    ) {
        this.shipmentId = shipmentId;
        this.trackingId = trackingId;
        this.operatorId = operatorId;
        this.packageId = packageId;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Integer getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Integer shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Integer getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Integer trackingId) {
        this.trackingId = trackingId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}