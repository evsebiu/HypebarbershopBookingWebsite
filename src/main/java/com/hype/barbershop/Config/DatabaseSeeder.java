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
    public void run(String... args) throws Exception{
        // verify if already exists an admin to avoid duplicate create

        if (barberRepo.findByEmail("admin@hype.ro").isEmpty()){
// 2. FRIZER 1 - OVIDIU
            if (barberRepo.findByEmail("ovidiu@hype.ro").isEmpty()) {
                Barber ovidiu = new Barber();
                ovidiu.setFirstName("Ovidiu");
                ovidiu.setLastName("Popescu");
                ovidiu.setEmail("ovidiu@hype.ro");
                ovidiu.setPassword(passwordEncoder.encode("hype123")); // Parola simpla
                ovidiu.setRole(Role.ROLE_BARBER);
                ovidiu.setIsActive(true);

                // Salvam intai frizerul ca sa avem ID-ul pentru servicii
                Barber savedOvidiu = barberRepo.save(ovidiu);

                // Cream serviciile pentru Ovidiu
                createService("Tuns Clasic", 50.0, 30, savedOvidiu);
                createService("Aranjat Barba", 30.0, 20, savedOvidiu);
                createService("Pachet Full (Tuns+Barba)", 75.0, 60, savedOvidiu);

                System.out.println("✅ Frizerul Ovidiu si serviciile lui au fost create.");
            }

            // 3. FRIZER 2 - CATALIN
            if (barberRepo.findByEmail("catalin@hype.ro").isEmpty()) {
                Barber catalin = new Barber();
                catalin.setFirstName("Catalin");
                catalin.setLastName("Ionescu");
                catalin.setEmail("catalin@hype.ro");
                catalin.setPassword(passwordEncoder.encode("hype123"));
                catalin.setRole(Role.ROLE_BARBER);
                catalin.setIsActive(true);

                Barber savedCatalin = barberRepo.save(catalin);

                // Cream serviciile pentru Catalin (poate are preturi diferite sau servicii diferite)
                createService("Tuns Fade", 60.0, 45, savedCatalin);
                createService("Barba Spa", 40.0, 30, savedCatalin);
                createService("Pachet VIP", 90.0, 75, savedCatalin);

                System.out.println("✅ Frizerul Catalin si serviciile lui au fost create.");
            }
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
}
