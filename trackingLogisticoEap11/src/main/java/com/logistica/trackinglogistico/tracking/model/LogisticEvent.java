package com.logistica.trackinglogistico.tracking.model;

import com.logistica.trackinglogistico.users.model.Operator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "eventologistico")
public class LogisticEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento")
    private Integer id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "idenvio", nullable = false)
    private Shipment shipment;

    @Setter
    @ManyToOne
    @JoinColumn(name = "idoperador", nullable = false)
    private Operator operator;

    
    @Setter
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Setter
    @Column(name = "ubicacion", nullable = false)
    private String location;

    @Setter
    @Column(name = "horaevento", nullable = false)
    private LocalDateTime eventDate;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "tipoevento", nullable = false)
    private ShipmentStatus eventType;

    public LogisticEvent() {
    }

}