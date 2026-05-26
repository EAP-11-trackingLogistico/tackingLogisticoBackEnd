package com.logistica.trackinglogistico.tracking.repository;

import com.logistica.trackinglogistico.tracking.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    Optional<Shipment> findByTrackingId(String trackingId);
    boolean existsByTrackingId(String trackingId);
}
