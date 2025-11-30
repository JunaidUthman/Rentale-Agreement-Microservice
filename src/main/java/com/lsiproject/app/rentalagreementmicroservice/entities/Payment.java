package com.lsiproject.app.rentalagreementmicroservice.entities;
import com.lsiproject.app.rentalagreementmicroservice.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * Entité de l'historique des paiements.
 * Cette table est principalement alimentée par l'écoute des événements 'RentPaid' du Smart Contract.
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayment;

    // Lien vers l'entité RentalContract (Clé étrangère)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_contract_id", nullable = false)
    private RentalContract rentalContract;

    @Column(nullable = false)
    private Double amount; // Montant payé (en unité locale ou en wei/ether converti)

    @Column(nullable = false, unique = true)
    private String txHash; // Transaction Hash Ethereum (Assure l'unicité et l'audit)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Date de l'horodatage du bloc Ethereum (source de vérité)
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // ID de l'utilisateur qui a effectué le paiement (Tenant)
    @Column(nullable = false)
    private Long tenantId;
}
