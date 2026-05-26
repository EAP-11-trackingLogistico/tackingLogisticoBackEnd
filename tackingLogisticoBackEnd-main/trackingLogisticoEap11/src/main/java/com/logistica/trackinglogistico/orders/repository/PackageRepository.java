package com.logistica.trackinglogistico.orders.repository;

import com.logistica.trackinglogistico.orders.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Integer> {
}