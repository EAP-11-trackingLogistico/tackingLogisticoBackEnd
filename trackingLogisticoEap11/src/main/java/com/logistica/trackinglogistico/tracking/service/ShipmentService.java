package com.logistica.trackinglogistico.tracking.service;

import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.repository.PackageRepository;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.tracking.dto.RegisterShipmentRequest;
import com.logistica.trackinglogistico.tracking.dto.ShipmentResponse;
import com.logistica.trackinglogistico.tracking.dto.StatusUpdateRequest;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import com.logistica.trackinglogistico.tracking.model.ShipmentStatus;
import com.logistica.trackinglogistico.tracking.repository.ShipmentRepository;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import com.logistica.trackinglogistico.users.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OperatorRepository operatorRepository;
    private final PersonRepository personRepository;
    private final PackageRepository packageRepository;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            OperatorRepository operatorRepository,
            PersonRepository personRepository,
            PackageRepository packageRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.operatorRepository = operatorRepository;
        this.personRepository = personRepository;
        this.packageRepository = packageRepository;
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Page<Shipment> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable);
    }

    public ShipmentResponse getShipmentByTrackingId(String trackingId) {
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el envío con trackingId: " + trackingId));

        return mapToResponse(shipment, "Consulta exitosa");
    }

   

    @Transactional
    public ShipmentResponse updateStatus(String trackingId, StatusUpdateRequest request) {
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el envío con trackingId: " + trackingId));

        ShipmentStatus newStatus = ShipmentStatus.fromString(request.getStatus());

        Package pkg = shipment.getPaquete();
        pkg.setEstado(newStatus.name());
        packageRepository.save(pkg);

        Shipment updatedShipment = shipmentRepository.save(shipment);

        return mapToResponse(updatedShipment, "Estado actualizado correctamente");
    }

    @Transactional
public ShipmentResponse registerShipment(RegisterShipmentRequest request) {
    Operator operator = operatorRepository.findById(request.getOperatorId())
            .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con id: " + request.getOperatorId()));

    Person sender = new Person();
    sender.setNombre(request.getSender().getNombre());
    sender.setDireccion(request.getSender().getDireccion());
    sender.setTelefono(request.getSender().getTelefono());
    sender = personRepository.save(sender);

    Person recipient = new Person();
    recipient.setNombre(request.getRecipient().getNombre());
    recipient.setDireccion(request.getRecipient().getDireccion());
    recipient.setTelefono(request.getRecipient().getTelefono());
    recipient = personRepository.save(recipient);

    Package packageEntity = new Package();
    packageEntity.setRemitente(sender);
    packageEntity.setDestinatario(recipient);
    packageEntity.setPeso(BigDecimal.valueOf(request.getPackageData().getPeso()));
    packageEntity.setEstado("REGISTERED");
    packageEntity = packageRepository.save(packageEntity);

    Shipment shipment = new Shipment();
    shipment.setTrackingId(generateUniqueTrackingId());
    shipment.setOperador(operator);
    shipment.setPaquete(packageEntity);
    shipment.setCreatedAt(LocalDateTime.now());

    Shipment savedShipment = shipmentRepository.save(shipment);

    return mapToResponse(savedShipment, "Envío registrado correctamente");
}

    private String generateUniqueTrackingId() {
        String trackingId;
        do {
            int number = ThreadLocalRandom.current().nextInt(100000, 1000000);
            trackingId = String.valueOf(number);
        } while (shipmentRepository.existsByTrackingId(trackingId));
        return trackingId;
    }

    private ShipmentResponse mapToResponse(Shipment shipment, String message) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getTrackingId(),
                shipment.getOperador().getIdOperador(),
                shipment.getPaquete().getIdPaquete(),
                shipment.getCreatedAt(),
                message
        );
    }
}