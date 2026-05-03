package com.logistica.trackinglogistico.users.service;

import com.logistica.trackinglogistico.shared.exception.ResourceAlreadyExistsException;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.dto.CreatePersonRequest;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Person getById(Integer id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id: " + id));
    }

    public Person create(CreatePersonRequest request) {

        if (personRepository.existsByTelefono(request.getTelefono())) {
            throw new ResourceAlreadyExistsException("Persona con teléfono " + request.getTelefono() + " ya existe");
        }

        Person person = new Person();
        person.setNombre(request.getNombre());
        person.setDireccion(request.getDireccion());
        person.setTelefono(request.getTelefono());

        return personRepository.save(person);
    }
}