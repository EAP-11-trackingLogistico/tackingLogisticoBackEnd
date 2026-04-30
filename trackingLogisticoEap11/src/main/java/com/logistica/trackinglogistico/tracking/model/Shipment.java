package com.logistica.trackinglogistico.tracking.model;

import com.logistica.trackinglogistico.orders.model.Package;
import com.logistica.trackinglogistico.users.model.Operator;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "envio")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idenvio")
    private Integer id;

    @Column(name = "numseguimiento", nullable = false, unique = true)
    private Integer trackingId;

    @ManyToOne
    @JoinColumn(name = "idoperador", nullable = false)
    private Operator operador;

    @ManyToOne
    @JoinColumn(name = "idpaquete", nullable = false)
    private Package paquete;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private ShipmentStatus status;

    @Column(name = "fecharegistro", nullable = false)
    private LocalDateTime createdAt;

    public Shipment() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Integer trackingId) {
        this.trackingId = trackingId;
    }

    public Operator getOperador() {
        return operador;
    }

    public void setOperador(Operator operador) {
        this.operador = operador;
    }

    public Package getPaquete() {
        return paquete;
    }

    public void setPaquete(Package paquete) {
        this.paquete = paquete;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}