package com.logistica.trackinglogistico.users.controller;

import com.logistica.trackinglogistico.users.dto.CreatePersonRequest;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAll() {
        return personService.getAll();
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable Integer id) {
        return personService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person create(@Valid @RequestBody CreatePersonRequest request) {
        return personService.create(request);
    }
}