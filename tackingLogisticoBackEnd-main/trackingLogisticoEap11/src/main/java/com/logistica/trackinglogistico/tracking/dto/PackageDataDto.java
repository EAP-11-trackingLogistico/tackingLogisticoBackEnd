package com.logistica.trackinglogistico.tracking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PackageDataDto {

    @NotNull
    private Double peso;

}