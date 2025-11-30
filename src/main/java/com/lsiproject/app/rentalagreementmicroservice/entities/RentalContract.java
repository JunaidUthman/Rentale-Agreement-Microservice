package com.lsiproject.app.rentalagreementmicroservice.entities;

import com.lsiproject.app.rentalagreementmicroservice.enums.RentalContractState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité du contrat de location (Étape 3, 4 et 5).
 * C'est l'enregistrement hors-chaîne (off-chain) de l'accord sur la blockchain.
 */
@Entity
@Table(name = "rental_contracts")
@Data
@NoArgsConstructor
public class RentalContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContract;

    // NOUVEAU: Lien direct vers l'ID du contrat sur la blockchain (agreementId)
    @Column(nullable = false, unique = true)
    private Long agreementIdOnChain;

    // ID du propriétaire (Microservice Auth)
    @Column(nullable = false)
    private Long ownerId;

    // ID du locataire (Microservice Auth)
    @Column(nullable = false)
    private Long tenantId;

    // ID de la propriété (Microservice Property)
    @Column(nullable = false)
    private Long propertyId;


    @Column(nullable = false)
    private Double securityDeposit;

    @Column(nullable = false)
    private Double rentPerMonth; // Montant du loyer mensuel (pour les paiements hors-chaîne)

    @Column(nullable = false)
    private LocalDate startDate; // Date de début du contrat

    @Column(nullable = false)
    private LocalDate endDate; // Date de fin du contrat

    @Column(nullable = false)
    private Boolean isKeyDelivered = false; // Confirmé par le locataire (déclenche activateAgreement)

    @Column(nullable = false)
    private Boolean isPaymentReleased = false; // Vrai après activateAgreement (le 1er loyer est transféré)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalContractState state; // État de l'enum RentalContractState (ex: ACTIVE, PENDING_RESERVATION)

    // Liste des paiements associés à ce contrat
    @OneToMany(mappedBy = "rentalContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> paymentHistory;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}