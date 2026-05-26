package com.logistica.trackinglogistico.users.controller;

import com.logistica.trackinglogistico.users.dto.CreateOperatorRequest;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.service.OperatorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operators")
public class OperatorController {

    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping
    public List<Operator> getAll() {
        return operatorService.getAll();
    }

    @GetMapping("/{id}")
    public Operator getById(@PathVariable Integer id) {
        return operatorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Operator create(@Valid @RequestBody CreateOperatorRequest request) {
        return operatorService.create(request);
    }
}