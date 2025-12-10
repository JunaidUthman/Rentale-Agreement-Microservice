package com.lsiproject.app.rentalagreementmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RentalAgreementMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalAgreementMicroserviceApplication.class, args);
    }

}
