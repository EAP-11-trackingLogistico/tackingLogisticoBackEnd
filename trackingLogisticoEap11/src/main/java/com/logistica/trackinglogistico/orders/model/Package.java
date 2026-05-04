package com.logistica.trackinglogistico.orders.model;

import com.logistica.trackinglogistico.users.model.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "paquete")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpaquete")
    private Integer idPaquete;

    @ManyToOne
    @JoinColumn(name = "idremitente", nullable = false)
    private Person remitente;

    @ManyToOne
    @JoinColumn(name = "iddestinatario", nullable = false)
    private Person destinatario;

    @Column(name = "peso", nullable = false)
    private BigDecimal peso;

    @Column(name = "estado", nullable = false)
    private String estado;

    public Package() {
        // Constructor vacío requerido por JPA
    }

}