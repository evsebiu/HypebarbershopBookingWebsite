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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/barbers/register").hasRole("ADMIN")

                        // Orice altceva necesită autentificare
                        .anyRequest().authenticated()
                )
                // AICI ESTE SCHIMBAREA MAJORĂ:
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
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


    // metoda pentru migrare
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowCredentials(true);
            }
        };
    }
}