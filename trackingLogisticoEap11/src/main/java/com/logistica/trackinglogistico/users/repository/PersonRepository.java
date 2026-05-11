package com.logistica.trackinglogistico.users.repository;

import com.logistica.trackinglogistico.users.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    boolean existsByTelefono(String telefono);
}