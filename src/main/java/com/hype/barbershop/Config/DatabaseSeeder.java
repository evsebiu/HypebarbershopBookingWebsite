package com.hype.barbershop.Config;

import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final BarberRepository barberRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception{
        // verify if already exists an admin to avoid duplicate create

        if (barberRepo.findByEmail("admin@hype.ro").isEmpty()){

            Barber admin = new Barber();
            admin.setFirstName("Admin");
            admin.setLastName("Sef");
            admin.setEmail("admin@hype.ro");
            admin.setPassword(passwordEncoder.encode("admin123?"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setIsActive(true);

            barberRepo.save(admin);
            System.out.println("Contul de ADMIN a fost creat : admin@hype.ro / admin123?");
        }
    }
}
