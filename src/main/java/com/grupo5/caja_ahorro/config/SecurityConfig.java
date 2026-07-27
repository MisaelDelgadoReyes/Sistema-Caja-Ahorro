package com.grupo5.caja_ahorro.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .cors(cors -> cors.configurationSource(
                corsConfigurationSource()
            ))

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/logout",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginProcessingUrl("/login")

                .successHandler(
                    (request, response, authentication) -> {
                        response.setStatus(
                            HttpStatus.OK.value()
                        );

                        response.setCharacterEncoding(
                            "UTF-8"
                        );

                        response.setContentType(
                            "application/json"
                        );

                        response.getWriter().write(
                            """
                            {
                                "mensaje": "Inicio de sesión correcto",
                                "usuario": "%s"
                            }
                            """.formatted(
                                authentication.getName()
                            )
                        );
                    }
                )

                .failureHandler(
                    (request, response, exception) -> {
                        response.setStatus(
                            HttpStatus.UNAUTHORIZED.value()
                        );

                        response.setCharacterEncoding(
                            "UTF-8"
                        );

                        response.setContentType(
                            "application/json"
                        );

                        response.getWriter().write(
                            """
                            {
                                "mensaje": "Usuario o contraseña incorrectos"
                            }
                            """
                        );
                    }
                )

                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")

                .logoutSuccessHandler(
                    (request, response, authentication) -> {
                        response.setStatus(
                            HttpStatus.OK.value()
                        );

                        response.setCharacterEncoding(
                            "UTF-8"
                        );

                        response.setContentType(
                            "application/json"
                        );

                        response.getWriter().write(
                            """
                            {
                                "mensaje": "Sesión cerrada correctamente"
                            }
                            """
                        );
                    }
                )

                .permitAll()
            )

            .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration =
            new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "http://localhost:5174"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}