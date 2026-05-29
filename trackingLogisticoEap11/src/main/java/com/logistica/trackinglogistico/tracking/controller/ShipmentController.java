package com.logistica.trackinglogistico.tracking.controller;

import com.logistica.trackinglogistico.tracking.dto.MovementHistoryResponse;
import com.logistica.trackinglogistico.tracking.dto.RegisterShipmentRequest;
import com.logistica.trackinglogistico.tracking.dto.ShipmentResponse;
import com.logistica.trackinglogistico.tracking.dto.StatusUpdateRequest;
import com.logistica.trackinglogistico.tracking.dto.TrackingResponse;
import com.logistica.trackinglogistico.tracking.model.Shipment;
import com.logistica.trackinglogistico.tracking.service.LogisticEventService;
import com.logistica.trackinglogistico.tracking.service.ShipmentService;
import jakarta.validation.Valid;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final LogisticEventService logisticEventService;

    public ShipmentController(ShipmentService shipmentService, LogisticEventService logisticEventService) {
        this.shipmentService = shipmentService;
        this.logisticEventService = logisticEventService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "API funcionando";
    }

    @GetMapping
    public CollectionModel<EntityModel<Shipment>> getAllShipments() {
        List<EntityModel<Shipment>> shipments = shipmentService.getAllShipments().stream()
                .map(s -> EntityModel.of(s,
                        linkTo(methodOn(ShipmentController.class)
                                .getShipmentByTrackingId(s.getTrackingId())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(shipments,
                linkTo(methodOn(ShipmentController.class).getAllShipments()).withSelfRel());
    }

    @GetMapping("/{trackingId}")
    public EntityModel<TrackingResponse> getShipmentByTrackingId(@PathVariable String trackingId) {
        TrackingResponse response = logisticEventService.getTracking(trackingId);

        return EntityModel.of(response,
                linkTo(methodOn(ShipmentController.class)
                        .getShipmentByTrackingId(trackingId)).withSelfRel(),
                linkTo(methodOn(ShipmentController.class)
                        .getMovementHistory(trackingId)).withRel("history"));
    }

    @GetMapping("/{trackingId}/history")
    public EntityModel<MovementHistoryResponse> getMovementHistory(
            @PathVariable String trackingId) {
        MovementHistoryResponse response = logisticEventService.getMovementHistory(trackingId);

        return EntityModel.of(response,
                linkTo(methodOn(ShipmentController.class)
                        .getMovementHistory(trackingId)).withSelfRel(),
                linkTo(methodOn(ShipmentController.class)
                        .getShipmentByTrackingId(trackingId)).withRel("shipment"));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<ShipmentResponse> registerShipment(
            @Valid @RequestBody RegisterShipmentRequest request) {
        ShipmentResponse response = shipmentService.registerShipment(request);

        return EntityModel.of(response,
                linkTo(methodOn(ShipmentController.class)
                        .getShipmentByTrackingId(response.getTrackingId())).withSelfRel());
    }

    @PatchMapping("/{trackingId}/status")
    public EntityModel<ShipmentResponse> updateStatus(
            @PathVariable String trackingId,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        ShipmentResponse response = shipmentService.updateStatus(trackingId, request);

        return EntityModel.of(response,
                linkTo(methodOn(ShipmentController.class)
                        .getShipmentByTrackingId(trackingId)).withSelfRel());
    }
}
