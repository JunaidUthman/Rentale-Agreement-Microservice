package com.lsiproject.app.rentalagreementmicroservice.properties;


import com.lsiproject.app.rentalagreementmicroservice.entities.RentalRequest;
import com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {

    // Trouver toutes les requêtes d'un locataire (tenant)
    List<RentalRequest> findByTenantId(Long tenantId);

    // Trouver toutes les requêtes pour une propriété spécifique
    List<RentalRequest> findByPropertyId(Long propertyId);

    // Trouver toutes les requêtes pour une propriété dans un certain statut (ex: PENDING)
    List<RentalRequest> findByPropertyIdAndStatus(Long propertyId, RentalRequestStatus status);

    // Trouver la requête ACCEPTED unique pour une propriété (pour créer le contrat).
    Optional<RentalRequest> findTopByPropertyIdAndStatus(Long propertyId, RentalRequestStatus status);

    // Trouver si un locataire a déjà une requête ACCEPTED ou PENDING pour cette propriété
    boolean existsByPropertyIdAndTenantIdAndStatusIn(Long propertyId, Long tenantId, List<RentalRequestStatus> statuses);
}
