package com.lsiproject.app.rentalagreementmicroservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class RentalContractCreationDto {
    private Long rentalRequestId;
    private Long agreementIdOnChain;
    private Long propertyId;
    private Long ownerId; // L'ID du propriétaire

    // Termes du contrat
    private Double securityDeposit;
    private Double rentPerMonth;
    private LocalDate startDate;
    private LocalDate endDate;

    // Le montant initial payé (doit être vérifié par le service)
    private Double initialPaymentAmount;

    // Le Transaction Hash du paiement initial (pour l'audit et la synchronisation)
    private String initialTxHash;
}
