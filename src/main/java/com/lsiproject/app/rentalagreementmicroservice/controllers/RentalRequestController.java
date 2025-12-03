package com.lsiproject.app.rentalagreementmicroservice.controllers;


import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestCreationDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestStatusUpdateDto;
import com.lsiproject.app.rentalagreementmicroservice.security.UserPrincipal;
import com.lsiproject.app.rentalagreementmicroservice.services.RentalRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Contrôleur REST pour gérer le cycle de vie des demandes de location (RentalRequest).
 */
@RestController
@RequestMapping("/api/v1/rental-requests")
public class RentalRequestController {

    private final RentalRequestService rentalRequestService;

    public RentalRequestController(RentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    /**
     * POST /api/v1/rental-requests
     * Étape 1: Crée une nouvelle demande de location (Statut PENDING).
     * Autorisation : Tout utilisateur authentifié.
     */
    @PostMapping
    public ResponseEntity<RentalRequestDto> createRentalRequest(
            @RequestBody RentalRequestCreationDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        RentalRequestDto createdRequest = rentalRequestService.createRequest(dto, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    /**
     * GET /api/v1/rental-requests/{id}
     * Récupère une demande par ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentalRequestDto> getRequestById(@PathVariable Long id) {
        RentalRequestDto request = rentalRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }
    /**
     * GET /api/v1/rental-requests/
     * Récupère tous les de;qndes
     */
    @GetMapping
    public ResponseEntity<List<RentalRequestDto>> getRequestById() {
        List<RentalRequestDto> request = rentalRequestService.getAllRequests();
        return ResponseEntity.ok(request);
    }

    /**
     * PUT /api/v1/rental-requests/{id}/status
     * Étape 2: Met à jour le statut (ACCEPTED, REJECTED, EXPIRED).
     * Autorisation : Tout utilisateur authentifié (Landlord dans la logique métier du service).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RentalRequestDto> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody RentalRequestStatusUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        RentalRequestDto updatedRequest = rentalRequestService.updateRequestStatus(id, dto, principal);
        return ResponseEntity.ok(updatedRequest);
    }

    /**
     * DELETE /api/v1/rental-requests/{id}
     * Supprime une demande.
     * Autorisation : Tout utilisateur authentifié (Tenant ou Landlord dans la logique métier du service).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalRequest(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        rentalRequestService.deleteRequest(id, principal);
        return ResponseEntity.noContent().build();
    }
}
