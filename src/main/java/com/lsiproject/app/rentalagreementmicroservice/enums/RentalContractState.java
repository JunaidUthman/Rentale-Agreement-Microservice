package com.lsiproject.app.rentalagreementmicroservice.enums;

public enum RentalContractState {
    PENDING_RESERVATION, // Paiement initial (Escrow) effectué, en attente de la remise des clés
    ACTIVE,              // Contrat actif, clé remise, paiements mensuels en cours
    CLOSED,              // Contrat terminé à la date de fin
    TERMINATED,          // Résiliation anticipée
    DISPUTED             // Litige en cours
}