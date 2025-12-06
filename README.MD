
# Rental Agreement API Documentation (Concise)

Ce microservice gère le cycle de vie des accords de location, des demandes initiales (RentalRequest) au suivi des contrats (RentalContract) et à l'historique des paiements (Payment). Il est au cœur de la logique d'Escrow (séquestre) du projet.

## Base Endpoint

* Toutes les requêtes utilisent le chemin de base : `/api/v1`

## Authentication

* Tous les endpoints sont protégés par JWT Bearer Token dans le header `Authorization`.
* L'API Gateway vérifie la validité du token, et ce microservice extrait les informations de l'utilisateur.

---

### A. Rental Request Endpoints (Gestion des Demandes de Location)

* Objectif : Gérer l'intérêt du locataire (Étape 1 & 2 du flux).

* Endpoints et opérations :
	* **Créer (POST)**: `/rental-requests` — Crée une nouvelle demande de location pour une propriété (Statut : `PENDING`).
	* **Lire (GET)**: `/rental-requests/{id}` — Récupère une demande par son ID.
	* **Mettre à jour Statut (PUT)**: `/rental-requests/{id}/status` — Met à jour le statut (`ACCEPTED`, `REJECTED`). Logique métier : rejette toutes les autres requêtes `PENDING` si celle-ci est `ACCEPTED`.
	* **Supprimer (DELETE)**: `/rental-requests/{id}` — Supprime une demande.

* Modèles de données (DTO) :
	* `RentalRequestCreationDto` — (contient `propertyId`).
	* `RentalRequestStatusUpdateDto` — (contient `status` de type Enum).
	* `RentalRequestDto` — (inclut `idRequest`, `tenantId`, `propertyId`, `status`).

---

### B. Rental Contract Endpoints (Gestion des Contrats - Escrow)

* Objectif : Gérer l'état du contrat en BDD après le paiement initial (Escrow) et son activation (remise de clé).

* Endpoints et opérations :
	* **Créer (POST)**: `/rental-contracts` — Crée l'enregistrement du contrat dans la BDD. Statut initial : `PENDING_RESERVATION`. Simule l'enregistrement après le paiement initial sur la blockchain.
	* **Lire (GET)**: `/rental-contracts/{id}` — Récupère un contrat par son ID local.
	* **Lire par Utilisateur (GET)**: `/rental-contracts/user/me` — Récupère tous les contrats où l'utilisateur est `tenantId` ou `ownerId`.
	* **Confirmer Clé (PUT)**: `/rental-contracts/{id}/key-delivery` — Étape 4. Mise à jour du statut `isKeyDelivered` à `true`. Mise à jour automatique de l'état du contrat à `ACTIVE` et `isPaymentReleased` à `true` (simulant la libération du premier loyer).

* Modèles de données (DTO) :
	* `RentalContractCreationDto` — (contient `rentalRequestId`, `propertyId`, `ownerId`, `securityDeposit`, `rentPerMonth`, `startDate`, `endDate`).
	* `KeyDeliveryUpdateDto` — (contient `isKeyDelivered`).
	* `RentalContractDto` — (inclut `agreementIdOnChain`, `state`, `isKeyDelivered`, etc.).

---

### C. Payment Endpoints (Historique des Paiements)

* Objectif : Servir de registre d'audit hors-chaîne pour toutes les transactions financières (loyers et initial).

* Endpoints et opérations :
	* **Créer (POST)**: `/payments` — Enregistrement audit. Ajoute une ligne dans l'historique des paiements. Note : cet endpoint est destiné à être appelé par le service d'écoute blockchain lors de la réception d'un événement `RentPaid`.
	* **Lire par ID (GET)**: `/payments/{id}` — Récupère un enregistrement de paiement spécifique.
	* **Lire par Contrat (GET)**: `/payments/contract/{contractId}` — Récupère tout l'historique des paiements pour un contrat donné.

* Modèles de données (DTO) :
	* `PaymentCreationDto` — (contient `rentalContractId`, `amount`, `txHash`, `timestamp`). Le `txHash` est la clé d'unicité.
	* `PaymentDto` — (inclut `idPayment`, `txHash`, `status`, `timestamp`).

---

### D. Architecture et Communication Inter-Services

* Découverte de service : utilise Spring Cloud Eureka (`@LoadBalanced WebClient`) pour interroger les autres microservices.
* Dépendance externe : nécessite le microservice Property (sous le nom `property-service` dans Eureka) pour valider l'existence et la disponibilité des propriétés.
* Sécurité : l'identité utilisateur est extraite du JWT (claims `sub`/`walletAddress` et `userId`) par le filtre `JwtAuthFilter`.