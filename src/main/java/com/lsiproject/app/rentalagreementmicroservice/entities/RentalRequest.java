package com.lsiproject.app.rentalagreementmicroservice.entities;

import com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité de la demande de location (Étape 1 & 2).
 * C'est le signal d'intérêt d'un locataire pour une propriété.
 */
@Entity
@Table(name = "rental_requests")
@Data
@NoArgsConstructor
public class RentalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRequest;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalRequestStatus status;

    // ID de l'utilisateur qui fait la demande (Tenant)
    @Column(nullable = false)
    private Long tenantId;

    // ID de la propriété concernée (Microservice Property)
    @Column(nullable = false)
    private Long propertyId;

}
