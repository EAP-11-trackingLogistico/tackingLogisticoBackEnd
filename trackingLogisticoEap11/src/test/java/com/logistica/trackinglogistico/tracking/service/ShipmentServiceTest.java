package com.logistica.trackinglogistico.tracking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.repository.PackageRepository;
import com.logistica.trackinglogistico.shared.exception.BadRequestException;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.tracking.dto.RegisterShipmentRequest;
import com.logistica.trackinglogistico.tracking.dto.PackageDataDto;
import com.logistica.trackinglogistico.tracking.dto.SenderRecipientDto;
import com.logistica.trackinglogistico.tracking.dto.ShipmentResponse;
import com.logistica.trackinglogistico.tracking.dto.StatusUpdateRequest;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import com.logistica.trackinglogistico.tracking.repository.ShipmentRepository;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import com.logistica.trackinglogistico.users.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentDao;

    @Mock
    private OperatorRepository operatorDao;

    @Mock
    private PersonRepository personDao;

    @Mock
    private PackageRepository packageDao;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment shipment;
    private Operator operator;
    private Package paquete;

    @BeforeEach
    void setUp() {
        operator = new Operator();
        operator.setIdOperador(1);
        operator.setNombre("Juan");
        operator.setUsuario("juan123");

        Person remitente = new Person();
        remitente.setIdPersona(1);
        remitente.setNombre("Ana");
        remitente.setDireccion("Calle 123");
        remitente.setTelefono("3001234567");

        Person destinatario = new Person();
        destinatario.setIdPersona(2);
        destinatario.setNombre("Carlos");
        destinatario.setDireccion("Calle 456");
        destinatario.setTelefono("3007654321");

        paquete = new Package();
        paquete.setIdPaquete(1);
        paquete.setRemitente(remitente);
        paquete.setDestinatario(destinatario);
        paquete.setPeso(BigDecimal.valueOf(2.5));
        paquete.setEstado("REGISTERED");

        shipment = new Shipment();
        shipment.setTrackingId("TRK-001");
        shipment.setOperador(operator);
        shipment.setPaquete(paquete);
        shipment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllShipmentsTest() {
        when(shipmentDao.findAll()).thenReturn(List.of(shipment));

        List<Shipment> result = shipmentService.getAllShipments();

        assertEquals(1, result.size());
        assertEquals("TRK-001", result.get(0).getTrackingId());
    }

    @Test
    void getShipmentByTrackingIdTest() {
        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));

        ShipmentResponse result = shipmentService.getShipmentByTrackingId("TRK-001");

        assertNotNull(result);
        assertEquals("TRK-001", result.getTrackingId());
        assertEquals("Consulta exitosa", result.getMessage());
    }

    @Test
    void getShipmentByTrackingIdNotFoundTest() {
        when(shipmentDao.findByTrackingId("TRK-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> shipmentService.getShipmentByTrackingId("TRK-999"));
    }

    @Test
    void updateStatusTest() {
        StatusUpdateRequest dtoStatus = new StatusUpdateRequest();
        dtoStatus.setStatus("IN_TRANSIT");

        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));
        when(packageDao.save(any(Package.class))).thenReturn(paquete);
        when(shipmentDao.save(any(Shipment.class))).thenReturn(shipment);

        ShipmentResponse result = shipmentService.updateStatus("TRK-001", dtoStatus);

        assertNotNull(result);
        assertEquals("TRK-001", result.getTrackingId());
        assertEquals("Estado actualizado correctamente", result.getMessage());
    }

    @Test
    void updateStatusNotFoundTest() {
        StatusUpdateRequest dtoStatus = new StatusUpdateRequest();
        dtoStatus.setStatus("IN_TRANSIT");

        when(shipmentDao.findByTrackingId("TRK-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> shipmentService.updateStatus("TRK-999", dtoStatus));
    }

    @Test
    void updateStatusInvalidStatusTest() {
        StatusUpdateRequest dtoStatus = new StatusUpdateRequest();
        dtoStatus.setStatus("ESTADO_INVALIDO");

        when(shipmentDao.findByTrackingId("TRK-001")).thenReturn(Optional.of(shipment));

        assertThrows(BadRequestException.class,
            () -> shipmentService.updateStatus("TRK-001", dtoStatus));
    }

    @Test
    void registerShipmentTest() {
        SenderRecipientDto dtoRemitente = new SenderRecipientDto();
        dtoRemitente.setNombre("Ana");
        dtoRemitente.setDireccion("Calle 123");
        dtoRemitente.setTelefono("3001234567");

        SenderRecipientDto dtoDestinatario = new SenderRecipientDto();
        dtoDestinatario.setNombre("Carlos");
        dtoDestinatario.setDireccion("Calle 456");
        dtoDestinatario.setTelefono("3007654321");

        PackageDataDto dtoPackage = new PackageDataDto();
        dtoPackage.setPeso(2.5);

        RegisterShipmentRequest dtoShipment = new RegisterShipmentRequest();
        dtoShipment.setOperatorId(1);
        dtoShipment.setSender(dtoRemitente);
        dtoShipment.setRecipient(dtoDestinatario);
        dtoShipment.setPackageData(dtoPackage);

        when(operatorDao.findById(1)).thenReturn(Optional.of(operator));
        when(personDao.save(any(Person.class))).thenReturn(new Person());
        when(packageDao.save(any(Package.class))).thenReturn(paquete);
        when(shipmentDao.existsByTrackingId(any())).thenReturn(false);
        when(shipmentDao.save(any(Shipment.class))).thenReturn(shipment);

        ShipmentResponse result = shipmentService.registerShipment(dtoShipment);

        assertNotNull(result);
        assertEquals("TRK-001", result.getTrackingId());
        assertEquals("Envío registrado correctamente", result.getMessage());
    }

    @Test
    void registerShipmentOperatorNotFoundTest() {
        RegisterShipmentRequest dtoShipment = new RegisterShipmentRequest();
        dtoShipment.setOperatorId(99);

        when(operatorDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> shipmentService.registerShipment(dtoShipment));
    }
}