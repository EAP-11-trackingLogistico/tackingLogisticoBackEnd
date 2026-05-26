package com.logistica.trackinglogistico.orders.service;

import com.logistica.trackinglogistico.orders.dto.CreatePackageRequest;
import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.orders.repository.PackageRepository;
import com.logistica.trackinglogistico.shared.exception.ResourceNotFoundException;
import com.logistica.trackinglogistico.users.model.Person;
import com.logistica.trackinglogistico.users.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final PersonRepository personRepository;

    public PackageService(PackageRepository packageRepository, PersonRepository personRepository) {
        this.packageRepository = packageRepository;
        this.personRepository = personRepository;
    }

    public List<Package> getAll() {
        return packageRepository.findAll();
    }

    public Package getById(Integer id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paquete no encontrado con id: " + id));
    }

    public Package create(CreatePackageRequest request) {
        Person remitente = personRepository.findById(request.getIdRemitente())
                .orElseThrow(() -> new ResourceNotFoundException("Remitente no encontrado con id: " + request.getIdRemitente()));

        Person destinatario = personRepository.findById(request.getIdDestinatario())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatario no encontrado con id: " + request.getIdDestinatario()));

        Package packageEntity = new Package();
        packageEntity.setRemitente(remitente);
        packageEntity.setDestinatario(destinatario);
        packageEntity.setPeso(request.getPeso());

        return packageRepository.save(packageEntity);
    }
}