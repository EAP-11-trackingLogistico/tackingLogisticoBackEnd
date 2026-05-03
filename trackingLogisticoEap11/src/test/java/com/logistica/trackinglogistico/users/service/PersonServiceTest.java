package com.logistica.trackinglogistico.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistica.trackinglogistico.shared.exception.ResourceAlreadyExistsException;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.dto.CreatePersonRequest;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personDao;

    @InjectMocks
    private PersonService personService;

    private Person dbPerson;
    private CreatePersonRequest newPerson;

    @BeforeEach
    void setUp() {
        dbPerson = new Person();
        dbPerson.setIdPersona(1);
        dbPerson.setNombre("Ana");
        dbPerson.setTelefono("3332516679");
        dbPerson.setDireccion("Calle 123");

        newPerson = new CreatePersonRequest();
        newPerson.setNombre("Ana");
        newPerson.setTelefono("3332516679");
        newPerson.setDireccion("Calle 123");
    }

    @Test
    void getAllPersonsTest() {
        Person person1 = new Person();
        person1.setIdPersona(1);
        person1.setNombre("Luisa");
        person1.setTelefono("3332516679");
        person1.setDireccion("Calle 123");

        Person person2 = new Person();
        person2.setIdPersona(2);
        person2.setNombre("Daniel");
        person2.setTelefono("3332456609");
        person2.setDireccion("Calle 456");

        when(personDao.findAll()).thenReturn(List.of(person1, person2));

        List<Person> result = personService.getAll();

        assertEquals(2, result.size());
        assertEquals("Luisa", result.get(0).getNombre());
        assertEquals("Daniel", result.get(1).getNombre());
    }

    @Test
    void getPersonByIdTest() {
        when(personDao.findById(1)).thenReturn(Optional.of(dbPerson));

        Person result = personService.getById(1);

        assertEquals(1, result.getIdPersona());
        assertEquals("Ana", result.getNombre());
        assertEquals("3332516679", result.getTelefono());
        assertEquals("Calle 123", result.getDireccion());
    }

    @Test
    void getPersonByIdNotFoundTest() {
        when(personDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personService.getById(99));
    }

    @Test
    void createPersonTest() {
        when(personDao.existsByTelefono("3332516679")).thenReturn(false);
        when(personDao.save(any(Person.class))).thenReturn(dbPerson);

        Person result = personService.create(newPerson);

        assertEquals(1, result.getIdPersona());
        assertEquals("Ana", result.getNombre());
        assertEquals("3332516679", result.getTelefono());
        assertEquals("Calle 123", result.getDireccion());
    }

    @Test
    void createExistingPersonTest() {
        when(personDao.existsByTelefono("3332516679")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> personService.create(newPerson));
    }
}