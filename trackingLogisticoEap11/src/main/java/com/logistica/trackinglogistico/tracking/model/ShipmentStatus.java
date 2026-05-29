package com.logistica.trackinglogistico.tracking.model;

import java.util.Arrays;

public enum ShipmentStatus {
    REGISTERED,
    IN_TRANSIT,
    AT_WAREHOUSE,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELAYED;

    public static ShipmentStatus fromString(String status) {
        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: " + Arrays.toString(values()), ex);
        }
    }
}