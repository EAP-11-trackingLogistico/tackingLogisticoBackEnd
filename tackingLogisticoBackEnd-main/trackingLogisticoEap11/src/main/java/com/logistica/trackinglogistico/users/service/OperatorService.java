package com.logistica.trackinglogistico.users.service;

import com.logistica.trackinglogistico.shared.exception.ResourceAlreadyExistsException;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.dto.CreateOperatorRequest;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorService {

    private final OperatorRepository operatorRepository;

    public OperatorService(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public List<Operator> getAll() {
        return operatorRepository.findAll();
    }

    public Operator getById(Integer id) {
        return operatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con id: " + id));
    }

    public Operator create(CreateOperatorRequest request) {

    if (operatorRepository.existsByUsuario(request.getUsuario())) {
        throw new ResourceAlreadyExistsException(
            "El usuario ya existe: " + request.getUsuario()
        );
    }

    Operator operator = new Operator();
    operator.setNombre(request.getNombre());
    operator.setUsuario(request.getUsuario());

    return operatorRepository.save(operator);
}

}