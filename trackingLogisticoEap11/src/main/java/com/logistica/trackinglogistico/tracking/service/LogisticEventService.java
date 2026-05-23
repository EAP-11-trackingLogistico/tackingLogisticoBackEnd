package com.logistica.trackinglogistico.tracking.service;

import com.logistica.trackinglogistico.shared.exception.BadRequestException;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.tracking.dto.CreateLogisticEventRequest;
import com.logistica.trackinglogistico.tracking.dto.LogisticEventResponse;
import com.logistica.trackinglogistico.tracking.dto.MovementEventItem;
import com.logistica.trackinglogistico.tracking.dto.MovementHistoryResponse;
import com.logistica.trackinglogistico.tracking.dto.TrackingResponse;
import com.logistica.trackinglogistico.tracking.model.LogisticEvent;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import com.logistica.trackinglogistico.tracking.model.ShipmentStatus;
import com.logistica.trackinglogistico.tracking.repository.LogisticEventRepository;
import com.logistica.trackinglogistico.tracking.repository.ShipmentRepository;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class LogisticEventService {

    private final LogisticEventRepository logisticEventRepository;
    private final ShipmentRepository shipmentRepository;
    private final OperatorRepository operatorRepository;

    public LogisticEventService(
            LogisticEventRepository logisticEventRepository,
            ShipmentRepository shipmentRepository,
            OperatorRepository operatorRepository
    ) {
        this.logisticEventRepository = logisticEventRepository;
        this.shipmentRepository = shipmentRepository;
        this.operatorRepository = operatorRepository;
    }

    @Transactional
    public LogisticEventResponse createEvent(CreateLogisticEventRequest request) {
        Shipment shipment = shipmentRepository.findByTrackingId(request.getTrackingId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe envío con trackingId: " + request.getTrackingId()));

        Operator operator = operatorRepository.findById(request.getOperatorId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe operador con id: " + request.getOperatorId()));

        ShipmentStatus eventType = parseStatus(request.getEventType());

        LogisticEvent event = new LogisticEvent();
        event.setShipment(shipment);
        event.setOperator(operator);
        event.setNombre(request.getNombre());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setEventType(eventType);

        shipmentRepository.save(shipment);

        LogisticEvent savedEvent = logisticEventRepository.save(event);

        return new LogisticEventResponse(
                savedEvent.getId(),
                savedEvent.getShipment().getTrackingId(),
                savedEvent.getOperator().getIdOperador(),
                savedEvent.getLocation(),
                savedEvent.getEventDate(),
                savedEvent.getEventType().name()
        );
    }

    public TrackingResponse getTracking(String trackingId) {
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe envío con trackingId: " + trackingId));

        String currentStatus = shipment.getPaquete().getEstado();

        LogisticEvent lastEvent = logisticEventRepository.findTopByShipmentOrderByEventDateDesc(shipment)
                .orElse(null);

        String lastLocation = lastEvent != null ? lastEvent.getLocation() : "Sin eventos registrados";

        return new TrackingResponse(
                shipment.getTrackingId(),
                currentStatus,
                lastLocation,
                lastEvent != null ? lastEvent.getEventDate() : null
        );
    }

    public MovementHistoryResponse getMovementHistory(String trackingId) {
        if (trackingId == null || trackingId.isBlank()) {
            throw new BadRequestException("El número de seguimiento no puede estar vacío");
        }

        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe envío con trackingId: " + trackingId));

        String currentStatus = shipment.getPaquete().getEstado();

        List<LogisticEvent> events = logisticEventRepository.findByShipmentOrderByEventDateAsc(shipment);

        List<MovementEventItem> eventItems = Collections.emptyList();
        String message;

        if (events.isEmpty()) {
            message = "No hay eventos registrados para este envío";
            log.info("Historial de movimientos consultado para trackingId: {} — sin eventos registrados", trackingId);
        } else {
            eventItems = events.stream()
                    .map(this::mapToMovementEventItem)
                    .toList();
            message = "Historial obtenido exitosamente";
            log.info("Historial de movimientos consultado para trackingId: {} — {} eventos encontrados", trackingId, events.size());
        }

        return new MovementHistoryResponse(
                shipment.getTrackingId(),
                currentStatus,
                eventItems.size(),
                message,
                eventItems
        );
    }

    private MovementEventItem mapToMovementEventItem(LogisticEvent event) {
        String operatorName = event.getOperator() != null ? event.getOperator().getNombre() : "Desconocido";

        return new MovementEventItem(
                event.getId(),
                event.getNombre(),
                event.getEventType().name(),
                event.getLocation(),
                event.getEventDate(),
                operatorName
        );
    }

    private ShipmentStatus parseStatus(String status) {
        try {
            return ShipmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Estado inválido. Usa: REGISTERED, IN_TRANSIT, AT_WAREHOUSE, OUT_FOR_DELIVERY, DELIVERED o DELAYED");
        }
    }
}