package com.logistica.trackinglogistico.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/shipments/ping").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/shipments/{trackingId}/history")
                    .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/shipments/{trackingId}")
                    .authenticated()
                .requestMatchers("/api/reports/**")
                    .hasAnyRole("ANALYST", "ADMIN")
                .requestMatchers("/api/shipments/register")
                    .hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers("/api/shipments/{trackingId}/status")
                    .hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers("/api/logistic-events/**")
                    .hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers("/api/operators/**")
                    .hasRole("ADMIN")
                .requestMatchers("/api/packages/**")
                    .hasAnyRole("OPERATOR", "ADMIN")
                .requestMatchers("/api/persons/**")
                    .hasAnyRole("OPERATOR", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
