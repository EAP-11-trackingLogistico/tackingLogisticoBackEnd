package com.logistica.trackinglogistico.users.repository;

import com.logistica.trackinglogistico.users.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    boolean existsByUsuario(String usuario);
}