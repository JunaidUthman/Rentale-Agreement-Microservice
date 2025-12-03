package com.lsiproject.app.rentalagreementmicroservice.dtos;

import com.lsiproject.app.rentalagreementmicroservice.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO pour afficher les détails d'un paiement enregistré.
 */
@Data
@Builder
public class PaymentDto {
    private Long idPayment;
    private Long rentalContractId;
    private Double amount;
    private String txHash;
    private PaymentStatus status;
    private LocalDateTime timestamp;
    private Long tenantId;
}
