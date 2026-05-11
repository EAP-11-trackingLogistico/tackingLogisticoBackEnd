package com.logistica.trackinglogistico.tracking.model;

import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.users.model.Operator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "envio")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idenvio")
    private Integer id;

    @Setter
    @Column(name = "numseguimiento", nullable = false, unique = true)
    private String trackingId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "idoperador", nullable = false)
    private Operator operador;

    @Setter
    @ManyToOne
    @JoinColumn(name = "idpaquete", nullable = false)
    private Package paquete;

    @Setter
    @Column(name = "fecharegistro", nullable = false)
    private LocalDateTime createdAt;

    public Shipment() {
        // Constructor vacío requerido por JPA
    }

}