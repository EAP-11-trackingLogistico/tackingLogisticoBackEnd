package com.logistica.trackinglogistico.security.dto;

public class AuthResponse {

    private String token;
    private String nombre;
    private String usuario;
    private String rol;

    public AuthResponse(String token, String nombre, String usuario, String rol) {
        this.token = token;
        this.nombre = nombre;
        this.usuario = usuario;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }
}
