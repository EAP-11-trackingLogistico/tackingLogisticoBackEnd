package com.logistica.trackinglogistico.users.controller;

import com.logistica.trackinglogistico.users.dto.CreateOperatorRequest;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.service.OperatorService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/operators")
public class OperatorController {

    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Operator>> getAll() {
        List<EntityModel<Operator>> operators = operatorService.getAll().stream()
                .map(o -> EntityModel.of(o,
                        linkTo(methodOn(OperatorController.class)
                                .getById(o.getIdOperador())).withSelfRel()))
                .toList();

        return CollectionModel.of(operators,
                linkTo(methodOn(OperatorController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Operator> getById(@PathVariable Integer id) {
        Operator operator = operatorService.getById(id);

        return EntityModel.of(operator,
                linkTo(methodOn(OperatorController.class).getById(id)).withSelfRel());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Operator> create(@Valid @RequestBody CreateOperatorRequest request) {
        Operator operator = operatorService.create(request);

        return EntityModel.of(operator,
                linkTo(methodOn(OperatorController.class)
                        .getById(operator.getIdOperador())).withSelfRel());
    }
}
