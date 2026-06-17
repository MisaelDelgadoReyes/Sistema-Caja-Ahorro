package com.grupo5.caja_ahorro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivamos CSRF (Crucial para APIs REST y para que Swagger pueda hacer POST/PUT sin fallar)
            .csrf(csrf -> csrf.disable())
            
            // 2. Reglas de autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // ¡Aquí liberamos Swagger y la documentación para todo tu equipo!
                .requestMatchers(
                    "/swagger-ui/**", 
                    "/v3/api-docs/**", 
                    "/swagger-ui.html", 
                    "/swagger-resources/**", 
                    "/webjars/**"
                ).permitAll()
                
                // Cualquier otra petición (los futuros endpoints de la Caja de Ahorro) requerirá estar logueado
                .anyRequest().authenticated()
            )
            
            // 3. Por ahora, mantenemos el formulario de login básico para las rutas protegidas
            .formLogin(form -> form.defaultSuccessUrl("/swagger-ui/index.html", true))
            .httpBasic(basic -> {});

        return http.build();
    }
}