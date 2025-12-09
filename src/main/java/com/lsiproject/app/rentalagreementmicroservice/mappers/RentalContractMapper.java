package com.lsiproject.app.rentalagreementmicroservice.mappers;

import com.lsiproject.app.rentalagreementmicroservice.dtos.RentalContractDto;
import com.lsiproject.app.rentalagreementmicroservice.entities.RentalContract;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour la conversion entre l'Entité JPA RentalContract et le DTO.
 */
@Component
public class RentalContractMapper {

    public RentalContractDto toDto(RentalContract entity) {
        return RentalContractDto.builder()
                .idContract(entity.getIdContract())
                .agreementIdOnChain(entity.getAgreementIdOnChain())
                .ownerId(entity.getOwnerId())
                .tenantId(entity.getTenantId())
                .propertyId(entity.getPropertyId())
                .securityDeposit(entity.getSecurityDeposit())
                .rentAmount(entity.getRentAmount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isKeyDelivered(entity.getIsKeyDelivered())
                .isPaymentReleased(entity.getIsPaymentReleased())
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .TotalAmountToPay(entity.getTotalAmountToPay())
                .PayedAmount(entity.getPayedAmount())
                .build();
    }

    public List<RentalContractDto> toDtoList(List<RentalContract> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
