package com.lsiproject.app.rentalagreementmicroservice.mappers;


import com.lsiproject.app.rentalagreementmicroservice.dtos.PaymentDto;
import com.lsiproject.app.rentalagreementmicroservice.entities.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour la conversion entre l'Entité JPA Payment et le DTO.
 */
@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment entity) {
        return PaymentDto.builder()
                .idPayment(entity.getIdPayment())
                .rentalContractId(entity.getRentalContract().getIdContract())
                .amount(entity.getAmount())
                .txHash(entity.getTxHash())
                .status(entity.getStatus())
                .timestamp(entity.getTimestamp())
                .tenantId(entity.getTenantId())
                .build();
    }

    public List<PaymentDto> toDtoList(List<Payment> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}