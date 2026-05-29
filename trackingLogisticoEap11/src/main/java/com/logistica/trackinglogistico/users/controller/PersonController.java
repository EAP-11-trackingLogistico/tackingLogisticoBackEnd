package com.logistica.trackinglogistico.users.controller;

import com.logistica.trackinglogistico.users.dto.CreatePersonRequest;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Person>> getAll() {
        List<EntityModel<Person>> persons = personService.getAll().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PersonController.class)
                                .getById(p.getIdPersona())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(persons,
                linkTo(methodOn(PersonController.class).getAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Person> getById(@PathVariable Integer id) {
        Person person = personService.getById(id);

        return EntityModel.of(person,
                linkTo(methodOn(PersonController.class).getById(id)).withSelfRel());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Person> create(@Valid @RequestBody CreatePersonRequest request) {
        Person person = personService.create(request);

        return EntityModel.of(person,
                linkTo(methodOn(PersonController.class)
                        .getById(person.getIdPersona())).withSelfRel());
    }
}
