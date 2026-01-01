package com.hype.barbershop.Config;

import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.ServiceDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ServiceDetailsRepository serviceRepo;
    private final BarberRepository barberRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // verify if already exists an admin to avoid duplicate create
        if (barberRepo.findByEmail("catalin@hype.ro").isEmpty()) {
            Barber admin = new Barber();
            admin.setFirstName("Catalin");
            admin.setLastName("Lc");
            admin.setEmail("catalin@hype.ro");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setIsActive(true);
            barberRepo.save(admin);
            System.out.println("✅ Admin created");
        }

        if (barberRepo.findByEmail("ovidiu@hype.ro").isEmpty()) {
            Barber barber = new Barber();
            barber.setFirstName("Ovidiu");
            barber.setLastName("Ciobanu");
            barber.setEmail("ovidiu@hype.ro");
            barber.setPassword(passwordEncoder.encode("ovidiu123"));
            barber.setRole(Role.ROLE_BARBER);
            barber.setIsActive(true);
            barberRepo.save(barber);
            System.out.println("Barber created.");
        }
    }

    private void createService(String name, Double price, Integer duration, Barber barber) {
        ServiceDetails service = new ServiceDetails();
        service.setServiceName(name);
        service.setPrice(price);
        service.setDuration(duration);
        service.setBarber(barber); // Legam serviciul de frizer
        serviceRepo.save(service);
    }
}
