package com.logistica.trackinglogistico.tracking.controller;

import com.logistica.trackinglogistico.tracking.dto.CreateLogisticEventRequest;
import com.logistica.trackinglogistico.tracking.dto.LogisticEventResponse;
import com.logistica.trackinglogistico.tracking.service.LogisticEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logistic-events")
public class LogisticEventController {

    private final LogisticEventService logisticEventService;

    public LogisticEventController(LogisticEventService logisticEventService) {
        this.logisticEventService = logisticEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogisticEventResponse createEvent(@Valid @RequestBody CreateLogisticEventRequest request) {
        return logisticEventService.createEvent(request);
    }
}