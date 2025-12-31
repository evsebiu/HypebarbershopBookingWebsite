package com.hype.barbershop.Repository;

import com.hype.barbershop.Model.DTO.ServiceDetailsDTO;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDetailsRepository extends JpaRepository<ServiceDetails, Long> {


    List<ServiceDetails> findByBarberId(Long barberId);
    List<ServiceDetails> findByServiceNameContainingIgnoreCase(String serviceName);
    List<ServiceDetails> findByPrice(Double price);
    List<ServiceDetails> findByDuration(Integer duration);
    boolean existsByServiceNameAndIdNot(String serviceName, Long id);
}
