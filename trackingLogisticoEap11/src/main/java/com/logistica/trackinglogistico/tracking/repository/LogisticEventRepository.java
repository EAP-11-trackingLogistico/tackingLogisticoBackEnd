package com.logistica.trackinglogistico.tracking.repository;

import com.logistica.trackinglogistico.tracking.model.LogisticEvent;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LogisticEventRepository extends JpaRepository<LogisticEvent, Integer> {

    List<LogisticEvent> findByShipmentOrderByEventDateDesc(Shipment shipment);

    Optional<LogisticEvent> findTopByShipmentOrderByEventDateDesc(Shipment shipment);
}