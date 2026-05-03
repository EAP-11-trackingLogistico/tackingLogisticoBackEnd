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
import com.logistica.trackinglogistico.users.dto.CreateOperatorRequest;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;

@ExtendWith(MockitoExtension.class)
public class OperatorServiceTest {

    @Mock
    private OperatorRepository operatorDao;

    @InjectMocks
    private OperatorService operatorService;

    private Operator savedOperator;
    private CreateOperatorRequest newOperator;

    @BeforeEach
    void setUp() {
        savedOperator = new Operator();
        savedOperator.setIdOperador(1);
        savedOperator.setNombre("Ana");
        savedOperator.setUsuario("ana123");

        newOperator = new CreateOperatorRequest();
        newOperator.setNombre("Ana");
        newOperator.setUsuario("ana123");
    }

    @Test
    void getAllOperatorsTest() {
        Operator operator1 = new Operator();
        operator1.setIdOperador(1);
        operator1.setNombre("Luisa");
        operator1.setUsuario("Luisa 123");

        Operator operator2 = new Operator();
        operator2.setIdOperador(2);
        operator2.setNombre("Carlos");
        operator2.setUsuario("Carlos123");

        when(operatorDao.findAll()).thenReturn(List.of(operator1, operator2));

        List<Operator> result = operatorService.getAll();

        assertEquals(2, result.size());
        assertEquals("Luisa", result.get(0).getNombre());
        assertEquals("Carlos", result.get(1).getNombre());
    }

    @Test
    void getOperatorByIdTest() {
        when(operatorDao.findById(1)).thenReturn(Optional.of(savedOperator));

        Operator result = operatorService.getById(1);

        assertEquals(1, result.getIdOperador());
        assertEquals("Ana", result.getNombre());
        assertEquals("ana123", result.getUsuario());
    }

    @Test
    void getOperatorByIdNotFoundTest() {
        when(operatorDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> operatorService.getById(1));
    }

    @Test
    void createOperatorTest() {
        when(operatorDao.existsByUsuario("ana123")).thenReturn(false);
        when(operatorDao.save(any(Operator.class))).thenReturn(savedOperator);

        Operator result = operatorService.create(newOperator);

        assertEquals(1, result.getIdOperador());
        assertEquals("Ana", result.getNombre());
        assertEquals("ana123", result.getUsuario());
    }

    @Test
    void createExistingOperatorTest() {
        when(operatorDao.existsByUsuario("ana123")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> operatorService.create(newOperator));
    }
}