package com.lsiproject.app.rentalagreementmicroservice.openFeignClients;

import com.lsiproject.app.rentalagreementmicroservice.dtos.PropertyResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "PropertyMicroService",
        url = "http://localhost:8082"
)
public interface PropertyMicroService {

    @GetMapping("/api/property-microservice/properties/{id}")
    PropertyResponseDTO getPropertyById(@PathVariable Long id);
}
