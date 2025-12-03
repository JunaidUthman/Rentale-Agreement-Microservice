package com.lsiproject.app.rentalagreementmicroservice.controllers;

import com.lsiproject.app.rentalagreementmicroservice.dtos.PaymentCreationDto;
import com.lsiproject.app.rentalagreementmicroservice.dtos.PaymentDto;
import com.lsiproject.app.rentalagreementmicroservice.security.UserPrincipal;
import com.lsiproject.app.rentalagreementmicroservice.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des paiements (Historique et Enregistrement).
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // --- CREATE Operation ---

    /**
     * POST /api/v1/payments
     * Enregistre un nouveau paiement.
     * ATTENTION: Dans une architecture réelle, cet endpoint devrait être très fortement sécurisé (par exemple,
     * par une clé API interne) car il est typiquement appelé par le service d'écoute blockchain, pas directement
     * par le frontend. Pour la démo, nous utilisons 'isAuthenticated()'.
     *
     * @param dto Les détails du paiement (txHash obligatoire).
     * @return ResponseEntity avec le paiement créé.
     */
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentCreationDto dto) {
        PaymentDto createdPayment = paymentService.createPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    // --- DISPLAY Operations ---

    /**
     * GET /api/v1/payments/{id}
     * Récupère un paiement par son ID interne.
     *
     * @param id L'ID du paiement interne.
     * @param principal L'utilisateur authentifié (pour vérification d'autorisation).
     * @return ResponseEntity avec les détails du paiement.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        PaymentDto payment = paymentService.getPaymentById(id, principal);
        return ResponseEntity.ok(payment);
    }

    /**
     * GET /api/v1/payments/contract/{contractId}
     * Récupère l'historique des paiements pour un contrat donné.
     *
     * @param contractId L'ID du contrat interne.
     * @param principal L'utilisateur authentifié (pour vérification d'autorisation).
     * @return ResponseEntity avec la liste des paiements.
     */
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<PaymentDto>> getPaymentHistoryByContract(@PathVariable Long contractId, @AuthenticationPrincipal UserPrincipal principal) {
        List<PaymentDto> history = paymentService.getPaymentHistoryByContract(contractId, principal);
        return ResponseEntity.ok(history);
    }
}