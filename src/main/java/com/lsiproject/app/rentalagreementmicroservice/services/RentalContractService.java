package com.lsiproject.app.rentalagreementmicroservice.services;

import com.lsiproject.app.rentalagreementmicroservice.dtos.KeyDeliveryUpdateDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalContractCreationDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalContractDto;
import com.lsiproject.app.rentalagreementmicroservice.entities.RentalContract;
import com.lsiproject.app.rentalagreementmicroservice.enums.RentalContractState;
import com.lsiproject.app.rentalagreementmicroservice.mappers.RentalContractMapper;
import com.lsiproject.app.rentalagreementmicroservice.properties.RentalContractRepository;
import com.lsiproject.app.rentalagreementmicroservice.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID; // Utilisé pour générer un ID temporaire

/**
 * Service pour la gestion des contrats de location (RentalContract) en BDD.
 * L'interaction avec la blockchain est intentionnellement omise à ce stade.
 */
@Service
public class RentalContractService {

    private final RentalContractRepository contractRepository;
    private final RentalContractMapper contractMapper;

    public RentalContractService(
            RentalContractRepository contractRepository,
            RentalContractMapper contractMapper) {
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
    }

    // =========================================================================================
    // READ Operations
    // =========================================================================================

    /**
     * Récupère un contrat par son ID interne.
     */
    public RentalContractDto getContractById(Long contractId) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental contract not found."));
        // L'autorisation (tenant/owner) doit être vérifiée par l'appelant
        return contractMapper.toDto(contract);
    }

    /**
     * Récupère tous les contrats d'un utilisateur (en tant que propriétaire ou locataire).
     */
    public List<RentalContractDto> getAllContractsForUser(Long userId) {
        // Trouver les contrats où l'utilisateur est propriétaire
        List<RentalContract> ownerContracts = contractRepository.findByOwnerId(userId);
        // Trouver les contrats où l'utilisateur est locataire
        List<RentalContract> tenantContracts = contractRepository.findByTenantId(userId);

        ownerContracts.addAll(tenantContracts);

        return contractMapper.toDtoList(ownerContracts);
    }

    // =========================================================================================
    // CREATE Operation (Déclenché par le paiement initial)
    // =========================================================================================

    /**
     * Crée un nouveau contrat dans la BDD après un paiement initial théorique (Étape 3).
     * Le statut initial est PENDING_RESERVATION.
     * @param dto Les termes du contrat.
     * @param principal L'utilisateur authentifié (Tenant).
     * @return Le contrat créé en BDD.
     */
    @Transactional
    public RentalContractDto createContract(RentalContractCreationDto dto, UserPrincipal principal) {

        // 1. Création de l'entité BDD
        RentalContract contract = new RentalContract();

        contract.setAgreementIdOnChain(dto.getAgreementIdOnChain());

        // Données du contrat
        contract.setOwnerId(dto.getOwnerId());
        contract.setTenantId(principal.getIdUser());
        contract.setPropertyId(dto.getPropertyId());

        contract.setSecurityDeposit(dto.getSecurityDeposit());
        contract.setRentPerMonth(dto.getRentPerMonth());
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());

        // Statuts initiaux requis
        contract.setIsKeyDelivered(false);
        contract.setIsPaymentReleased(false);
        contract.setState(RentalContractState.PENDING_RESERVATION);

        // 2. Sauvegarde
        contract = contractRepository.save(contract);


        return contractMapper.toDto(contract);
    }

    // =========================================================================================
    // UPDATE Operation (Confirmation de clé)
    // =========================================================================================

    /**
     * Met à jour le statut du contrat pour confirmer la remise de clé (Étape 4).
     * Cette fonction met à jour l'état dans la BDD sans interagir avec la blockchain.
     * @param contractId L'ID du contrat interne.
     * @param dto Confirmation de clé.
     * @param principal L'utilisateur authentifié (Tenant).
     * @return Le contrat mis à jour.
     */
    @Transactional
    public RentalContractDto updateKeyDeliveryStatus(Long contractId, KeyDeliveryUpdateDto dto, UserPrincipal principal) {
        RentalContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental contract not found."));


        System.out.println("the key boolean equals =============="+dto.getIsKeyDelivered());
        // 1. Vérification d'autorisation et de statut
        if (!contract.getTenantId().equals(principal.getIdUser())) {
            throw new AccessDeniedException("Only the tenant is authorized to confirm key delivery.");
        }
        if (contract.getState() != RentalContractState.PENDING_RESERVATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contract is not in PENDING_RESERVATION status.");
        }
        if (contract.getIsKeyDelivered()) {
            return contractMapper.toDto(contract); // Déjà livré, rien à faire
        }

        // 2. Mise à jour de l'état de la clé dans la BDD
        contract.setIsKeyDelivered(dto.getIsKeyDelivered());

        // 3. Mise à jour des statuts (simulant la conséquence de l'activation)
        if (dto.getIsKeyDelivered()) {
            contract.setState(RentalContractState.ACTIVE);
            contract.setIsPaymentReleased(true); // Le premier loyer est censé être libéré
        } else {
            // Si la livraison de clé est annulée (bien que peu probable dans ce flux)
            contract.setState(RentalContractState.PENDING_RESERVATION);
        }

        // 4. Sauvegarde et retour
        contract = contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }
}