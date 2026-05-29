package com.logistica.trackinglogistico.orders.controller;

import com.logistica.trackinglogistico.orders.dto.CreatePackageRequest;
import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Package>> getAll() {
        List<EntityModel<Package>> packages = packageService.getAll().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PackageController.class)
                                .getById(p.getIdPaquete())).withSelfRel()))
                .toList();

        return CollectionModel.of(packages,
                linkTo(methodOn(PackageController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Package> getById(@PathVariable Integer id) {
        Package pkg = packageService.getById(id);

        return EntityModel.of(pkg,
                linkTo(methodOn(PackageController.class).getById(id)).withSelfRel());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Package> create(@Valid @RequestBody CreatePackageRequest request) {
        Package pkg = packageService.create(request);

        return EntityModel.of(pkg,
                linkTo(methodOn(PackageController.class)
                        .getById(pkg.getIdPaquete())).withSelfRel());
    }
}
