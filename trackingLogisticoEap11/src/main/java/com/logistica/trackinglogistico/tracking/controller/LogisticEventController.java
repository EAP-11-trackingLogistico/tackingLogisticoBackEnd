package com.logistica.trackinglogistico.tracking.controller;

import com.logistica.trackinglogistico.tracking.dto.CreateLogisticEventRequest;
import com.logistica.trackinglogistico.tracking.dto.LogisticEventResponse;
import com.logistica.trackinglogistico.tracking.service.LogisticEventService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/logistic-events")
public class LogisticEventController {

    private final LogisticEventService logisticEventService;

    public LogisticEventController(LogisticEventService logisticEventService) {
        this.logisticEventService = logisticEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<LogisticEventResponse> createEvent(
            @Valid @RequestBody CreateLogisticEventRequest request) {
        LogisticEventResponse response = logisticEventService.createEvent(request);

        return EntityModel.of(response,
                linkTo(methodOn(ShipmentController.class)
                        .getMovementHistory(response.getTrackingId())).withRel("history"));
    }
}
