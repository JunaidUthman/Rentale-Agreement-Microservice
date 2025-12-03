package com.lsiproject.app.rentalagreementmicroservice.services;


import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestCreationDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestStatusUpdateDto;
import com.lsiproject.app.rentalagreementmicroservice.entities.RentalRequest;
import com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus;
import com.lsiproject.app.rentalagreementmicroservice.mappers.RentalRequestMapper;
import com.lsiproject.app.rentalagreementmicroservice.properties.RentalRequestRepository;
import com.lsiproject.app.rentalagreementmicroservice.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Arrays;

/**
 * Service pour la gestion des demandes de location (RentalRequest).
 */
@Service
public class RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;
    private final RentalRequestMapper rentalRequestMapper;

    public RentalRequestService(RentalRequestRepository rentalRequestRepository, RentalRequestMapper rentalRequestMapper) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.rentalRequestMapper = rentalRequestMapper;
    }

    /**
     * Crée une nouvelle demande de location (Étape 1).
     * @param dto Les données de création.
     * @param principal L'utilisateur authentifié (Tenant).
     * @return Le DTO de la demande créée.
     */
    public RentalRequestDto createRequest(RentalRequestCreationDto dto, UserPrincipal principal) {

        List<RentalRequestStatus> activeStatuses = Arrays.asList(RentalRequestStatus.PENDING, RentalRequestStatus.ACCEPTED);
        boolean alreadyActive = rentalRequestRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                dto.getPropertyId(), principal.getIdUser(), activeStatuses);

        if (alreadyActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Active rental request already exists for this user and property.");
        }

        // 2. Création de l'entité
        RentalRequest request = new RentalRequest();
        request.setPropertyId(dto.getPropertyId());
        request.setTenantId(principal.getIdUser());
        request.setStatus(RentalRequestStatus.PENDING); // Statut initial

        // 3. Sauvegarde et conversion
        request = rentalRequestRepository.save(request);
        return rentalRequestMapper.toDto(request);
    }

    /**
     * Récupère toutes les demandes pour une propriété spécifique.
     */
    public List<RentalRequestDto> findAllRequestsForProperty(Long propertyId) {
        return rentalRequestMapper.toDtoList(rentalRequestRepository.findByPropertyId(propertyId));
    }

    /**
     * Récupère toutes les demandes faites par un locataire.
     */
    public List<RentalRequestDto> findAllRequestsForTenant(Long tenantId) {
        return rentalRequestMapper.toDtoList(rentalRequestRepository.findByTenantId(tenantId));
    }

    /**
     * Récupère une demande par ID.
     */
    public RentalRequestDto getRequestById(Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental request not found."));
        return rentalRequestMapper.toDto(request);
    }


    public List<RentalRequestDto> getAllRequests(){
        return rentalRequestMapper.toDtoList(rentalRequestRepository.findAll());
    }
    /**
     * Met à jour le statut d'une demande (Étape 2).
     * @param requestId L'ID de la demande.
     * @param dto Le nouveau statut.
     * @param principal L'utilisateur authentifié.
     * @return Le DTO mis à jour.
     */
    @Transactional
    public RentalRequestDto updateRequestStatus(Long requestId, RentalRequestStatusUpdateDto dto, UserPrincipal principal) {
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental request not found."));

        // 1. Logique métier pour l'acceptation (Étape 2)
        if (dto.getStatus() == RentalRequestStatus.ACCEPTED) {
            // Règle métier: Si une requête est ACCEPTED, toutes les autres requêtes PENDING pour cette
            // propriété doivent être REJECTED.
            rejectOtherPendingRequests(request.getPropertyId(), requestId);
        }

        // 2. Mise à jour du statut (la seule mise à jour autorisée)
        request.setStatus(dto.getStatus());

        // 3. Sauvegarde et conversion
        request = rentalRequestRepository.save(request);
        return rentalRequestMapper.toDto(request);
    }

    /**
     * Fonction utilitaire pour rejeter les autres demandes en attente pour la même propriété.
     * @param propertyId ID de la propriété.
     * @param acceptedRequestId L'ID de la demande acceptée.
     */
    private void rejectOtherPendingRequests(Long propertyId, Long acceptedRequestId) {
        List<RentalRequest> pendingRequests = rentalRequestRepository.findByPropertyIdAndStatus(
                propertyId, RentalRequestStatus.PENDING);

        for (RentalRequest req : pendingRequests) {
            if (!req.getIdRequest().equals(acceptedRequestId)) {
                req.setStatus(RentalRequestStatus.REJECTED);
                rentalRequestRepository.save(req);
            }
        }
    }

    /**
     * Supprime une demande.
     */
    public void deleteRequest(Long requestId, UserPrincipal principal) {
        RentalRequest request = rentalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental request not found."));

        rentalRequestRepository.delete(request);
    }
}
