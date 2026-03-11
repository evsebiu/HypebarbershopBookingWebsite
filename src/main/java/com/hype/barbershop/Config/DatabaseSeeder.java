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
        // verify if already exists an admin to avoid duplicate create
        if (barberRepo.findByEmail("catalin@hype.ro").isEmpty()) {
            Barber admin = new Barber();
            admin.setFirstName("Catalin");
            admin.setLastName("Costin");
            admin.setEmail("catalin@hype.ro");
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
