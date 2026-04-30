package com.logistica.trackinglogistico.tracking.model;

import com.logistica.trackinglogistico.users.model.Operator;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "evento_logistico")
public class LogisticEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idenvio", nullable = false)
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "idoperador", nullable = false)
    private Operator operator;

    @ManyToOne
    @JoinColumn(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "ubicacion", nullable = false)
    private String location;

    @Column(name = "horaevento", nullable = false)
    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoevento", nullable = false)
    private ShipmentStatus eventType;

    public LogisticEvent() {
    }

    public Integer getId() {
        return id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public ShipmentStatus getEventType() {
        return eventType;
    }

    public void setEventType(ShipmentStatus eventType) {
        this.eventType = eventType;
    }
}