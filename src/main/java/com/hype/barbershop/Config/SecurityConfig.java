package com.hype.barbershop.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Resurse Statice (TREBUIE să fie publice ca să se încarce CSS-ul la login)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                        // 2. Pagini Publice (Site-ul principal)
                        .requestMatchers("/", "/index", "/error").permitAll()
                        .requestMatchers("/barber/**").permitAll() // Profiluri publice frizeri
                        .requestMatchers("/api/barbers/active").permitAll() // API pentru lista de frizeri
                        .requestMatchers("/cookies/**").permitAll()
                        .requestMatchers("/appointment/**").permitAll()
                        .requestMatchers("/terms/**").permitAll()
                        .requestMatchers("/gdpr/**").permitAll()
                        .requestMatchers("/api/appointments/**").permitAll()
                        .requestMatchers("/consumer-rights/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/manifest.json", "/sw.js", "/icons/**").permitAll()
                        .requestMatchers(HttpMethod.GET, ("/api/barbers/**")).permitAll()

                        // 3. Pagina de Login - Trebuie să fie publică
                        .requestMatchers("/login").permitAll()

                        // 4. ZONE SECURIZATE
                        // Dashboard-ul comun (accesibil și Adminilor și Frizerilor)
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "BARBER")

                        // Zone strict pentru Admin (dacă mai ai API-uri specifice)
                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/barbers/register").hasRole("ADMIN")

                        // Orice altceva necesită autentificare
                        .anyRequest().authenticated()
                )
                // AICI ESTE SCHIMBAREA MAJORĂ:
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/login")
                        // Dacă logarea reușește, trimitem un JSON 200 OK
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Login succesful\"}");
                        })
                        // Dacă logarea eșuează, trimitem un JSON 401 Unauthorized
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Email sau parola incorecta\"}");
                        })
                        .permitAll()
                )

                .rememberMe(remember -> remember
                        .key("Hq8#mP2$vL5xnR8@qW1!yZ4^tC7&bN0k") // O parolă internă a serverului
                        .userDetailsService(userDetailsService)
                        .tokenValiditySeconds(31536000) // Fix 365 de zile (1 an) în secunde
                        .alwaysRemember(true) // Forțează reținerea fără ca userul să bifeze un checkbox!
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


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitem și frontend-ul local (Vite) și domeniile de pe VPS
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "https://hypebarbershop.ro",
                "https://www.hypebarbershop.ro"
        ));

        // Permitem toate metodele HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permitem headerele necesare
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}