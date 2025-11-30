package com.lsiproject.app.rentalagreementmicroservice.dtos;

import com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour afficher les détails d'une demande de location.
 */
@Data
@Builder
public class RentalRequestDto {
    private Long idRequest;
    private LocalDateTime createdAt;
    private RentalRequestStatus status;
    private Long tenantId;
    private Long propertyId;
}