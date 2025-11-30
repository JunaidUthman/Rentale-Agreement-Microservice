package com.lsiproject.app.rentalagreementmicroservice.mappers;

import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalRequestDto;
import com.lsiproject.app.rentalagreementmicroservice.entities.RentalRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour la conversion entre l'Entité JPA RentalRequest et le DTO.
 */
@Component
public class RentalRequestMapper {

    /**
     * Convertit l'entité JPA en DTO de réponse.
     * @param entity L'entité RentalRequest.
     * @return Le DTO correspondant.
     */
    public RentalRequestDto toDto(RentalRequest entity) {
        return RentalRequestDto.builder()
                .idRequest(entity.getIdRequest())
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .tenantId(entity.getTenantId())
                .propertyId(entity.getPropertyId())
                .build();
    }

    /**
     * Convertit une liste d'entités en liste de DTO.
     * @param entities La liste des entités RentalRequest.
     * @return La liste de DTO correspondante.
     */
    public List<RentalRequestDto> toDtoList(List<RentalRequest> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
