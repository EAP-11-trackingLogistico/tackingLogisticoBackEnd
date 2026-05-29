package com.logistica.trackinglogistico.users.controller;

import com.logistica.trackinglogistico.shared.exception.GlobalExceptionHandler;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.dto.CreateOperatorRequest;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.service.OperatorService;
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

class OperatorControllerIntegrationTest {

    private MockMvc mockMvc;
    private OperatorService operatorService;

    @BeforeEach
    void setUp() {
        operatorService = mock(OperatorService.class);
        OperatorController controller = new OperatorController(operatorService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllShouldReturn200() throws Exception {
        Operator operator = new Operator();
        operator.setIdOperador(1);
        operator.setNombre("Juan");
        when(operatorService.getAll()).thenReturn(List.of(operator));

        mockMvc.perform(get("/api/operators"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEmptyShouldReturn200() throws Exception {
        when(operatorService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/operators"))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdShouldReturn200() throws Exception {
        Operator operator = new Operator();
        operator.setIdOperador(1);
        operator.setNombre("Juan");
        when(operatorService.getById(1)).thenReturn(operator);

        mockMvc.perform(get("/api/operators/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOperador").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void getByIdNotFoundShouldReturn404() throws Exception {
        when(operatorService.getById(99))
                .thenThrow(new ResourceNotFoundException("Operador no encontrado con id: 99"));

        mockMvc.perform(get("/api/operators/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Operator operator = new Operator();
        operator.setIdOperador(1);
        operator.setNombre("Juan");
        when(operatorService.create(any(CreateOperatorRequest.class))).thenReturn(operator);

        String body = """
                {
                    "nombre": "Juan",
                    "usuario": "juan123"
                }
                """;

        mockMvc.perform(post("/api/operators")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOperador").value(1));
    }

    @Test
    void createWithMissingFieldsShouldReturn400() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/operators")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
