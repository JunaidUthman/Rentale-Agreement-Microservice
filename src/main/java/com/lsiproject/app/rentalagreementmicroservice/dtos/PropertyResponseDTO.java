package com.lsiproject.app.rentalagreementmicroservice.dtos;

import com.lsiproject.app.rentalagreementmicroservice.enums.TypeOfRental;

import java.time.LocalDateTime;

public record PropertyResponseDTO(
        Long idProperty,
        Long onChainId,

        String title,
        String country,
        String city,
        String address,
        Double longitude,
        Double latitude,
        String description,
        TypeOfRental typeOfRental,

        Long rentAmount,
        Long securityDeposit,
        Boolean isAvailable,
        Boolean isActive,

        Long ownerId,
        String ownerEthAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
