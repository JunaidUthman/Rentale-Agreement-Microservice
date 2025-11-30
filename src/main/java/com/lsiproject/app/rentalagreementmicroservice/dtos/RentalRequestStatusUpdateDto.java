package com.lsiproject.app.rentalagreementmicroservice.dtos;

import com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus;
import lombok.Data;

/**
 * DTO pour la mise à jour du statut d'une demande de location (PUT /rental-requests/{id}).
 * Contient uniquement le nouveau statut.
 */
@Data
public class RentalRequestStatusUpdateDto {
    private RentalRequestStatus status;
}