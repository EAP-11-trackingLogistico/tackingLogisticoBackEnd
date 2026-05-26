package com.logistica.trackinglogistico.tracking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegisterShipmentRequest {

    @NotNull
    private Integer operatorId;

    @Valid
    @NotNull
    private SenderRecipientDto sender;

    @Valid
    @NotNull
    private SenderRecipientDto recipient;

    @Valid
    @NotNull
    private PackageDataDto packageData;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public SenderRecipientDto getSender() {
        return sender;
    }

    public void setSender(SenderRecipientDto sender) {
        this.sender = sender;
    }

    public SenderRecipientDto getRecipient() {
        return recipient;
    }

    public void setRecipient(SenderRecipientDto recipient) {
        this.recipient = recipient;
    }

    public PackageDataDto getPackageData() {
        return packageData;
    }

    public void setPackageData(PackageDataDto packageData) {
        this.packageData = packageData;
    }
}