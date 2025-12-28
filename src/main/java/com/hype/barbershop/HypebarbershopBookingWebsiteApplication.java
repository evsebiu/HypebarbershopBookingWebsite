package com.hype.barbershop;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "nextGen Hypebarbershop booking website",
                version = "1.0",
                description = "About\n" +
                        "NextGen Barbershop Management System This full-stack application is a streamlined booking solution" +
                        " engineered with Java Spring Boot, designed to modernize barbershop operations. It features a " +
                        "dual-interface architecture: a public landing page for frictionless client reservations and a secured " +
                        "administrative dashboard for business management."
        )
)

@SpringBootApplication
public class HypebarbershopBookingWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(HypebarbershopBookingWebsiteApplication.class, args);
	}

}
