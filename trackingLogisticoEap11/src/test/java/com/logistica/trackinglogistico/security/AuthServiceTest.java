package com.logistica.trackinglogistico.security;

import com.logistica.trackinglogistico.security.dto.AuthResponse;
import com.logistica.trackinglogistico.security.dto.LoginRequest;
import com.logistica.trackinglogistico.security.dto.RegisterRequest;
import com.logistica.trackinglogistico.shared.exception.BadRequestException;
import com.logistica.trackinglogistico.shared.exception.ResourceAlreadyExistsException;
import com.logistica.trackinglogistico.users.model.Operator;
import com.logistica.trackinglogistico.users.repository.OperatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Operator operator;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        operator = new Operator();
        operator.setIdOperador(1);
        operator.setNombre("Juan");
        operator.setUsuario("juan123");
        operator.setContrasena("encoded_password");
        operator.setRol("OPERATOR");

        loginRequest = new LoginRequest();
        loginRequest.setUsuario("juan123");
        loginRequest.setContrasena("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Nuevo Usuario");
        registerRequest.setUsuario("nuevo123");
        registerRequest.setContrasena("password123");
        registerRequest.setRol("ANALYST");
    }

    @Test
    void loginShouldReturnAuthResponse() {
        when(operatorRepository.findByUsuario("juan123")).thenReturn(Optional.of(operator));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken("juan123", "OPERATOR")).thenReturn("jwt_token");

        AuthResponse result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("jwt_token", result.getToken());
        assertEquals("juan123", result.getUsuario());
        assertEquals("OPERATOR", result.getRol());
    }

    @Test
    void loginWithInvalidPasswordShouldThrow() {
        when(operatorRepository.findByUsuario("juan123")).thenReturn(Optional.of(operator));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void loginWithUnknownUserShouldThrow() {
        when(operatorRepository.findByUsuario("juan123")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void registerShouldReturnAuthResponse() {
        when(operatorRepository.existsByUsuario("nuevo123")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(operatorRepository.save(any(Operator.class))).thenReturn(operator);
        when(jwtService.generateToken("juan123", "OPERATOR")).thenReturn("jwt_token");

        AuthResponse result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("jwt_token", result.getToken());
    }

    @Test
    void registerDuplicateShouldThrow() {
        when(operatorRepository.existsByUsuario("nuevo123")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void registerWithoutRoleShouldDefaultToOperator() {
        registerRequest.setRol(null);

        when(operatorRepository.existsByUsuario("nuevo123")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(operatorRepository.save(any(Operator.class))).thenReturn(operator);
        when(jwtService.generateToken("juan123", "OPERATOR")).thenReturn("jwt_token");

        AuthResponse result = authService.register(registerRequest);
        assertNotNull(result);
    }
}
