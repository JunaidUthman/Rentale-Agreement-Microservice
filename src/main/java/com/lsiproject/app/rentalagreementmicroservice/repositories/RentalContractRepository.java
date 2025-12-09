package com.lsiproject.app.rentalagreementmicroservice.repositories;


import com.lsiproject.app.rentalagreementmicroservice.entities.RentalContract;
import com.lsiproject.app.rentalagreementmicroservice.enums.RentalContractState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    // Trouver un contrat par son ID unique sur la blockchain
    Optional<RentalContract> findByAgreementIdOnChain(Long agreementIdOnChain);

    // Trouver tous les contrats pour un propriétaire
    List<RentalContract> findByOwnerId(Long ownerId);

    // Trouver tous les contrats pour un locataire
    List<RentalContract> findByTenantId(Long tenantId);

    // Trouver les contrats actifs ou en attente de remise de clé
    List<RentalContract> findByStateIn(List<RentalContractState> states);

    // Trouver les contrats nécessitant une action (par exemple, ACTIVE et endDate dépassée)
    List<RentalContract> findByStateAndEndDateBefore(RentalContractState state, java.time.LocalDate endDate);
}