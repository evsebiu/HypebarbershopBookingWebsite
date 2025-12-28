package com.hype.barbershop.Service;


import com.hype.barbershop.Exceptions.BarbershopException;
import com.hype.barbershop.Exceptions.BarbershopResourceNotFound;
import com.hype.barbershop.Model.Entity.Barber;
import com.hype.barbershop.Repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final BarberRepository barberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        // search barber in database by email
        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(()-> new BarbershopException("Utilizatorul nu a fost gasit in baza de date."));

        //convert ENUM ROLE to Spring format
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(barber.getRole().name());

        //return object user from Spring Security with barber details

        return new User(
                barber.getEmail(),
                barber.getPassword(), // encrypted pass from db
                Collections.singleton(authority)
        );
    }
}
