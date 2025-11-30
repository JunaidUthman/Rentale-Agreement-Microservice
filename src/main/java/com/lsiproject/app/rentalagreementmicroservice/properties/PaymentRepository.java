package com.lsiproject.app.rentalagreementmicroservice.properties;

import com.lsiproject.app.rentalagreementmicroservice.entities.Payment;
import com.lsiproject.app.rentalagreementmicroservice.entities.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Trouver tous les paiements pour un contrat spécifique
    List<Payment> findByRentalContract(RentalContract rentalContract);

    // Vérifier l'existence d'un paiement via son Transaction Hash (pour éviter la duplication des événements)
    boolean existsByTxHash(String txHash);

    // Trouver le dernier paiement effectué pour un contrat
    Optional<Payment> findTopByRentalContractOrderByTimestampDesc(RentalContract rentalContract);

    // Trouver les paiements par l'ID du locataire
    List<Payment> findByTenantId(Long tenantId);
}