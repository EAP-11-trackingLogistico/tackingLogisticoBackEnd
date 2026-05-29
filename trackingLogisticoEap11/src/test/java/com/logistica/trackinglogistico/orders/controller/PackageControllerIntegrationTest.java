package com.logistica.trackinglogistico.orders.controller;

import com.logistica.trackinglogistico.orders.dto.CreatePackageRequest;
import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.service.PackageService;
import com.logistica.trackinglogistico.shared.exception.GlobalExceptionHandler;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.model.Person;
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

class PackageControllerIntegrationTest {

    private MockMvc mockMvc;
    private PackageService packageService;

    @BeforeEach
    void setUp() {
        packageService = mock(PackageService.class);
        PackageController controller = new PackageController(packageService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllShouldReturn200() throws Exception {
        Package pkg = new Package();
        pkg.setIdPaquete(1);
        when(packageService.getAll()).thenReturn(List.of(pkg));

        mockMvc.perform(get("/api/packages"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEmptyShouldReturn200() throws Exception {
        when(packageService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/packages"))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdShouldReturn200() throws Exception {
        Package pkg = new Package();
        pkg.setIdPaquete(1);
        when(packageService.getById(1)).thenReturn(pkg);

        mockMvc.perform(get("/api/packages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaquete").value(1));
    }

    @Test
    void getByIdNotFoundShouldReturn404() throws Exception {
        when(packageService.getById(99))
                .thenThrow(new ResourceNotFoundException("Paquete no encontrado con id: 99"));

        mockMvc.perform(get("/api/packages/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn201() throws Exception {
        Package pkg = new Package();
        pkg.setIdPaquete(1);
        Person remitente = new Person();
        remitente.setNombre("Juan");
        pkg.setRemitente(remitente);
        when(packageService.create(any(CreatePackageRequest.class))).thenReturn(pkg);

        String body = """
                {
                    "idRemitente": 1,
                    "idDestinatario": 2,
                    "peso": 2.5
                }
                """;

        mockMvc.perform(post("/api/packages")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPaquete").value(1));
    }

    @Test
    void createWithMissingFieldsShouldReturn400() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/packages")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
