package com.logistica.trackinglogistico.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreatePackageRequest {

    @NotNull
    private Integer idRemitente;

    @NotNull
    private Integer idDestinatario;

    private BigDecimal peso;

}