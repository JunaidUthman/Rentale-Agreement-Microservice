package com.lsiproject.app.rentalagreementmicroservice.dtos;

import lombok.Data;

/**
 * DTO pour la création d'une demande de location (POST /rental-requests).
 * Nécessite uniquement l'ID de la propriété, le TenantId est pris du JWT.
 */
@Data
public class RentalRequestCreationDto {
    // ID de la propriété pour laquelle la demande est faite
    private Long propertyId;
}