package com.logistica.trackinglogistico.security;

import com.logistica.trackinglogistico.security.dto.AuthResponse;
import com.logistica.trackinglogistico.security.dto.LoginRequest;
import com.logistica.trackinglogistico.security.dto.RegisterRequest;
import com.logistica.trackinglogistico.shared.exception.BadRequestException;
import com.logistica.trackinglogistico.shared.exception.ResourceAlreadyExistsException;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "OPERATOR";

    private final OperatorRepository operatorRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(OperatorRepository operatorRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.operatorRepository = operatorRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        Operator operator = operatorRepository.findByUsuario(request.getUsuario())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getContrasena(), operator.getContrasena())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        String role = operator.getRol() != null ? operator.getRol() : DEFAULT_ROLE;
        String token = jwtService.generateToken(operator.getUsuario(), role);

        return new AuthResponse(token, operator.getNombre(), operator.getUsuario(), role);
    }

    public AuthResponse register(RegisterRequest request) {
        if (operatorRepository.existsByUsuario(request.getUsuario())) {
            throw new ResourceAlreadyExistsException(
                    "El usuario '" + request.getUsuario() + "' ya existe");
        }

        Operator operator = new Operator();
        operator.setNombre(request.getNombre());
        operator.setUsuario(request.getUsuario());
        operator.setContrasena(passwordEncoder.encode(request.getContrasena()));
        operator.setRol(request.getRol() != null ? request.getRol() : DEFAULT_ROLE);

        operator = operatorRepository.save(operator);

        String token = jwtService.generateToken(operator.getUsuario(), operator.getRol());

        return new AuthResponse(token, operator.getNombre(), operator.getUsuario(), operator.getRol());
    }
}
