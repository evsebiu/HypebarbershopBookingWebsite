package com.hype.barbershop.Config;

import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Model.Entity.BarberSchedule;
import com.hype.barbershop.Model.Entity.ServiceDetails;
import com.hype.barbershop.Model.Enums.Role;
import com.hype.barbershop.Repository.BarberRepository;
import com.hype.barbershop.Repository.BarberScheduleRepository;
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
    private final BarberScheduleRepository barberScheduleRepo;




    @Override
    public void run(String... args) throws Exception {

        // =======================================================
        // SCRIPT DE REPARARE AUTOMATĂ (Va rula o singură dată)
        // =======================================================
        Barber oldProfile = barberRepo.findByEmail("catalin@hype.ro").orElse(null);
        Barber newEmptyProfile = barberRepo.findByEmail("costincatalin199@gmail.com").orElse(null);

        // Dacă găsește AMBELE conturi, înseamnă că trebuie să facem "rocada"
        if (oldProfile != null && newEmptyProfile != null) {

            // 1. Schimbăm email-ul contului gol ca să "eliberăm" adresa de Gmail
            newEmptyProfile.setEmail("de_sters@hype.ro");
            newEmptyProfile.setIsActive(false); // Îl și dezactivăm să nu te încurce vizual
            barberRepo.save(newEmptyProfile);

            // 2. Acum că adresa e liberă, o punem pe profilul VECHI (cel care are toate programările)
            oldProfile.setEmail("costincatalin199@gmail.com");
            oldProfile.setPassword(passwordEncoder.encode("catalinbarberhype69"));
            barberRepo.save(oldProfile);

            System.out.println("✅ REPARAT: Profilul cu programări folosește acum noul email!");
        }
        // =======================================================



        // verify if already exists an admin to avoid duplicate create
        if (barberRepo.findByEmail("costincatalin199@gmail.com").isEmpty()) {
            Barber admin = new Barber();
            admin.setFirstName("Catalin");
            admin.setLastName("Costin");
            admin.setEmail("costincatalin199@gmail.com");
            admin.setPassword(passwordEncoder.encode("catalinbarberhype69"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setIsActive(true);
            barberRepo.save(admin);
            initializeDefaultSchedule(admin);
            System.out.println("✅ Admin created");
        }

        if (barberRepo.findByEmail("ovidiu@hype.ro").isEmpty()) {
            Barber barber = new Barber();
            barber.setFirstName("Ovidiu");
            barber.setLastName("Ciobanu");
            barber.setEmail("ovidiu@hype.ro");
            barber.setPassword(passwordEncoder.encode("ovidiuhypebarber99"));
            barber.setRole(Role.ROLE_BARBER);
            barber.setIsActive(true);
            barberRepo.save(barber);
            initializeDefaultSchedule(barber);
            System.out.println("Barber created.");
        }
    }


    private void initializeDefaultSchedule(Barber barber) {
        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
            BarberSchedule schedule = new BarberSchedule();
            schedule.setBarber(barber);
            schedule.setDayOfWeek(day);
            schedule.setStartTime(java.time.LocalTime.of(9, 0));
            schedule.setEndTime(java.time.LocalTime.of(18, 0));
            schedule.setIsWorkingDay(true);
            // barberScheduleRepository trebuie injectat în constructorul clasei
            barberScheduleRepo.save(schedule);
        }
    }


        private void createService (String name, Double price, Integer duration, Barber barber){
            ServiceDetails service = new ServiceDetails();
            service.setServiceName(name);
            service.setPrice(price);
            service.setDuration(duration);
            service.setBarber(barber); // Legam serviciul de frizer
            serviceRepo.save(service);
        }
    }
