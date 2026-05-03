package com.logistica.trackinglogistico.tracking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistica.trackinglogistico.shared.exception.BadRequestException;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.tracking.dto.CreateLogisticEventRequest;
import com.logistica.trackinglogistico.tracking.dto.LogisticEventResponse;
import com.logistica.trackinglogistico.tracking.dto.TrackingResponse;
import com.logistica.trackinglogistico.tracking.model.LogisticEvent;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import com.logistica.trackinglogistico.tracking.model.ShipmentStatus;
import com.logistica.trackinglogistico.tracking.repository.LogisticEventRepository;
import com.logistica.trackinglogistico.tracking.repository.ShipmentRepository;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import com.logistica.trackinglogistico.orders.model.Package;

@ExtendWith(MockitoExtension.class)
public class LogisticEventServiceTest {

    @Mock
    private LogisticEventRepository logisticEventDao;

    @Mock
    private ShipmentRepository shipmentDao;

    @Mock
    private OperatorRepository operatorDao;

    @InjectMocks
    private LogisticEventService logisticEventService;

    private Shipment shipment;
    private Operator operator;
    private LogisticEvent logisticEvent;
    private CreateLogisticEventRequest dtoEvent;

    @BeforeEach
    void setUp() {
        Package paquete = new Package();
        paquete.setEstado("REGISTERED");

        shipment = new Shipment();
        shipment.setTrackingId("TRK-001");
        shipment.setPaquete(paquete);

        operator = new Operator();
        operator.setIdOperador(1);
        operator.setNombre("Juan");
        operator.setUsuario("juan123");

        logisticEvent = new LogisticEvent();
        logisticEvent.setShipment(shipment);
        logisticEvent.setOperator(operator);
        logisticEvent.setNombre("Evento de prueba");
        logisticEvent.setLocation("Bodega Central");
        logisticEvent.setEventDate(LocalDateTime.now());
        logisticEvent.setEventType(ShipmentStatus.IN_TRANSIT);

        dtoEvent = new CreateLogisticEventRequest();
        dtoEvent.setTrackingId("TRK-001");
        dtoEvent.setOperatorId(1);
        dtoEvent.setNombre("Evento de prueba");
        dtoEvent.setLocation("Bodega Central");
        dtoEvent.setEventDate(LocalDateTime.now());
        dtoEvent.setEventType("IN_TRANSIT");
    }

    @Test
    void createEventTest() {
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));
        when(operatorDao.findById(1)).thenReturn(Optional.of(operator));
        when(logisticEventDao.save(any(LogisticEvent.class))).thenReturn(logisticEvent);

        LogisticEventResponse result = logisticEventService.createEvent(dtoEvent);

        assertNotNull(result);
        assertEquals("TRK-001", result.getTrackingId());
        assertEquals("Bodega Central", result.getLocation());
        assertEquals("IN_TRANSIT", result.getEventType());
    }

    @Test
    void createEventShipmentNotFoundTest() {
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> logisticEventService.createEvent(dtoEvent));
    }

    @Test
    void createEventOperatorNotFoundTest() {
        dtoEvent.setOperatorId(99);
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));
        when(operatorDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> logisticEventService.createEvent(dtoEvent));
    }

    @Test
    void createEventInvalidStatusTest() {
        dtoEvent.setEventType("ESTADO_INVALIDO");
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));
        when(operatorDao.findById(1)).thenReturn(Optional.of(operator));

        assertThrows(BadRequestException.class,
            () -> logisticEventService.createEvent(dtoEvent));
    }

    @Test
    void getTrackingTest() {
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));
        when(logisticEventDao.findTopByShipmentOrderByEventDateDesc(shipment))
            .thenReturn(Optional.of(logisticEvent));

        TrackingResponse result = logisticEventService.getTracking("TRK-001");

        assertNotNull(result);
        assertEquals("TRK-001", result.getTrackingId());
        assertEquals("Bodega Central", result.getLastLocation());
    }

    @Test
    void getTrackingNoEventsTest() {
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));
        when(logisticEventDao.findTopByShipmentOrderByEventDateDesc(shipment))
            .thenReturn(Optional.empty());

        TrackingResponse result = logisticEventService.getTracking("TRK-001");

        assertNotNull(result);
        assertEquals("Sin eventos registrados", result.getLastLocation());
        assertNull(result.getLastEventDate());
    }

    @Test
    void getTrackingNotFoundTest() {
        when(shipmentDao.findByTrackingId("TRK-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> logisticEventService.getTracking("TRK-999"));
    }
}