package com.logistica.trackinglogistico.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logistica.trackinglogistico.orders.dto.CreatePackageRequest;
import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.repository.PackageRepository;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PackageServiceTest {

    @Mock
    private PackageRepository packageDao;

    @Mock
    private PersonRepository personDao;

    @InjectMocks
    private PackageService packageService;

    private Person remitente, destinatario;
    private Package newPackage;
    private CreatePackageRequest dtoPackage;

    @BeforeEach
    void setUp() {
        remitente = new Person();
        remitente.setIdPersona(1);
        remitente.setNombre("Juan Pérez");
        remitente.setDireccion("Calle 123");
        remitente.setTelefono("123456789");

        destinatario = new Person();
        destinatario.setIdPersona(2);
        destinatario.setNombre("María García");
        destinatario.setDireccion("Calle 456");
        destinatario.setTelefono("987654321");

        newPackage = new Package();
        newPackage.setIdPaquete(1);
        newPackage.setRemitente(remitente);
        newPackage.setDestinatario(destinatario);
        newPackage.setPeso(BigDecimal.valueOf(2.5));

        dtoPackage = new CreatePackageRequest();
        dtoPackage.setIdRemitente(1);
        dtoPackage.setIdDestinatario(2);
        dtoPackage.setPeso(BigDecimal.valueOf(2.5));
    }

    @Test
    void getAllPackagesTest() {
        when(packageDao.findAll()).thenReturn(List.of(newPackage));

        List<Package> result = packageService.getAll();

        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getRemitente().getNombre());
        assertEquals("María García", result.get(0).getDestinatario().getNombre());
    }

    @Test
    void getPackageByIdTest() {
        when(packageDao.findById(1)).thenReturn(Optional.of(newPackage));

        Package result = packageService.getById(1);

        assertEquals(1, result.getIdPaquete());
        assertEquals("Juan Pérez", result.getRemitente().getNombre());
        assertEquals("María García", result.getDestinatario().getNombre());
    }

    @Test
    void getPackageByIdNotFoundTest() {
        when(packageDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> packageService.getById(99));
    }

    @Test
    void createPackageTest() {
        when(personDao.findById(1)).thenReturn(Optional.of(remitente));
        when(personDao.findById(2)).thenReturn(Optional.of(destinatario));
        when(packageDao.save(any(Package.class))).thenReturn(newPackage);

        Package result = packageService.create(dtoPackage);

        assertEquals(1, result.getIdPaquete());
        assertEquals("Juan Pérez", result.getRemitente().getNombre());
        assertEquals("María García", result.getDestinatario().getNombre());
    }

    @Test
    void senderNotFoundPackageTest() {
        dtoPackage.setIdRemitente(99);
        when(personDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> packageService.create(dtoPackage));
    }

    @Test
    void recipientNotFoundPackageTest() {
        dtoPackage.setIdDestinatario(99);
        when(personDao.findById(1)).thenReturn(Optional.of(remitente));
        when(personDao.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> packageService.create(dtoPackage));
    }
}