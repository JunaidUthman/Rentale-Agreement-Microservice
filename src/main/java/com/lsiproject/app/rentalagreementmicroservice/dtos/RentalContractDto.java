package com.lsiproject.app.rentalagreementmicroservice.dtos;

import com.lsiproject.app.rentalagreementmicroservice.enums.RentalContractState;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pour afficher les détails d'un contrat de location.
 */
@Data
@Builder
public class RentalContractDto {
    private Long idContract;
    private Long agreementIdOnChain; // ID du Smart Contract
    private Long ownerId;
    private Long tenantId;
    private Long propertyId;
    private Double securityDeposit;
    private Double rentPerMonth;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isKeyDelivered;
    private Boolean isPaymentReleased;
    private RentalContractState state;
    private LocalDateTime createdAt;
}
