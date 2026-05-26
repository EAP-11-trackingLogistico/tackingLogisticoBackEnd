package com.logistica.trackinglogistico.orders.controller;

import com.logistica.trackinglogistico.orders.dto.CreatePackageRequest;
import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping
    public List<Package> getAll() {
        return packageService.getAll();
    }

    @GetMapping("/{id}")
    public Package getById(@PathVariable Integer id) {
        return packageService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Package create(@Valid @RequestBody CreatePackageRequest request) {
        return packageService.create(request);
    }
}