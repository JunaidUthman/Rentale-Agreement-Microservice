package com.lsiproject.app.rentalagreementmicroservice.dtos;

import com.lsiproject.app.rentalagreementmicroservice.enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO utilisé pour enregistrer un paiement.
 * Ce DTO est typiquement utilisé par un service d'écoute blockchain
 * pour synchroniser les événements RentPaid.
 */
@Data
public class PaymentCreationDto {
    private Long rentalContractId; // ID interne du contrat BDD
    private Double amount;
    private String txHash; // Transaction Hash (clé d'audit et d'unicité)
    private PaymentStatus status; // Doit être CONFIRMED pour les événements RentPaid réussis
    private LocalDateTime timestamp;
    private Long tenantId;
}