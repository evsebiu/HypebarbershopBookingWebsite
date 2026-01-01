package com.hype.barbershop.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Resurse Statice (TREBUIE să fie publice ca să se încarce CSS-ul la login)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                        // 2. Pagini Publice (Site-ul principal)
                        .requestMatchers("/", "/index", "/error").permitAll()
                        .requestMatchers("/barber/**").permitAll() // Profiluri publice frizeri
                        .requestMatchers("/api/barbers/active").permitAll() // API pentru lista de frizeri

                        // 3. Pagina de Login - Trebuie să fie publică
                        .requestMatchers("/login").permitAll()

                        // 4. ZONE SECURIZATE
                        // Dashboard-ul comun (accesibil și Adminilor și Frizerilor)
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "BARBER")

                        // Zone strict pentru Admin (dacă mai ai API-uri specifice)
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/barbers/register").hasRole("ADMIN")

                        // Orice altceva necesită autentificare
                        .anyRequest().authenticated()
                )
                // AICI ESTE SCHIMBAREA MAJORĂ:
                .formLogin(form -> form
                        .loginPage("/login")              // Ruta controller-ului (vezi pasul 2)
                        .loginProcessingUrl("/login")     // Unde trimite formularul datele (POST)
                        .defaultSuccessUrl("/dashboard", true) // Redirecționare după succes
                        .failureUrl("/login?error=true")  // Unde te duce dacă greșești parola
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")        // După logout te duce pe prima pagină
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}