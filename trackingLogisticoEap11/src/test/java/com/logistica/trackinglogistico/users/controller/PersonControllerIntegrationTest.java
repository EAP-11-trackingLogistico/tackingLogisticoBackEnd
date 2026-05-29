package com.logistica.trackinglogistico.users.controller;

import com.logistica.trackinglogistico.shared.exception.GlobalExceptionHandler;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.dto.CreatePersonRequest;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PersonControllerIntegrationTest {

    private MockMvc mockMvc;
    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = mock(PersonService.class);
        PersonController controller = new PersonController(personService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllShouldReturn200() throws Exception {
        Person person = new Person();
        person.setIdPersona(1);
        person.setNombre("Juan");
        when(personService.getAll()).thenReturn(List.of(person));

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllEmptyShouldReturn200() throws Exception {
        when(personService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getByIdShouldReturn200() throws Exception {
        Person person = new Person();
        person.setIdPersona(1);
        person.setNombre("Juan");
        when(personService.getById(1)).thenReturn(person);

        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersona").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void getByIdNotFoundShouldReturn404() throws Exception {
        when(personService.getById(99))
                .thenThrow(new ResourceNotFoundException("Persona no encontrada con id: 99"));

        mockMvc.perform(get("/api/persons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Person person = new Person();
        person.setIdPersona(1);
        person.setNombre("Juan");

        when(personService.create(any(CreatePersonRequest.class))).thenReturn(person);

        String body = """
                {
                    "nombre": "Juan",
                    "direccion": "Calle 123",
                    "telefono": "3001234567"
                }
                """;

        mockMvc.perform(post("/api/persons")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPersona").value(1));
    }

    @Test
    void createWithMissingFieldsShouldReturn400() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/persons")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
