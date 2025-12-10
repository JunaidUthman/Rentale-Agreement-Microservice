package com.lsiproject.app.rentalagreementmicroservice.services;


import com.lsiproject.app.rentalagreementmicroservice.dtos.PropertyResponseDTO;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestCreationDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestStatusUpdateDto;
import com.lsiproject.app.rentalagreementmicroservice.entities.RentalRequest;
import com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus;
import com.lsiproject.app.rentalagreementmicroservice.mappers.RentalRequestMapper;
import com.lsiproject.app.rentalagreementmicroservice.openFeignClients.PropertyMicroService;
import com.lsiproject.app.rentalagreementmicroservice.repositories.RentalRequestRepository;
import com.lsiproject.app.rentalagreementmicroservice.security.UserPrincipal;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus.ACCEPTED;
import static com.lsiproject.app.rentalagreementmicroservice.enums.RentalRequestStatus.PENDING;

/**
 * Service pour la gestion des demandes de location (RentalRequest).
 */
@Service
public class RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;
    private final RentalRequestMapper rentalRequestMapper;
    private final PropertyMicroService propertyMicroService;

    public RentalRequestService(PropertyMicroService propertyMicroService, RentalRequestRepository rentalRequestRepository, RentalRequestMapper rentalRequestMapper) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.rentalRequestMapper = rentalRequestMapper;
        this.propertyMicroService =  propertyMicroService;
    }

    /**
     * Crée une nouvelle demande de location (Étape 1).
     * @param dto Les données de création.
     * @param principal L'utilisateur authentifié (Tenant).
     * @return Le DTO de la demande créée.
     */
    public RentalRequestDto createRequest(@Valid @RequestBody RentalRequestCreationDto dto, UserPrincipal principal) {


        PropertyResponseDTO property;

        try {
            property = propertyMicroService.getPropertyById(dto.getPropertyId());


            boolean propertyExists = rentalRequestRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                    property.idProperty(),
                    principal.getIdUser(),
                    new ArrayList<>(Arrays.asList(RentalRequestStatus.PENDING, RentalRequestStatus.ACCEPTED))
            );

            if(propertyExists){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "this property is aready requested for rental");
            }
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
        }


        List<RentalRequestStatus> activeStatuses = Arrays.asList(PENDING, ACCEPTED);
        boolean alreadyActive = rentalRequestRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                dto.getPropertyId(), principal.getIdUser(), activeStatuses);

        if (alreadyActive) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Active rental request already exists for this user and property.");
        }

        // 2. Création de l'entité
        RentalRequest request = new RentalRequest();
        request.setPropertyId(dto.getPropertyId());
        request.setTenantId(principal.getIdUser());
        request.setStatus(PENDING); // Statut initial

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
        if (dto.getStatus() == ACCEPTED) {
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
                propertyId, PENDING);

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
