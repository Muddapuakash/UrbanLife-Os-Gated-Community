package com.urbanlife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            UserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // ==========================================
    // AUTHENTICATION PROVIDER
    // ==========================================

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService);

        provider.setPasswordEncoder(
                passwordEncoder());

        return provider;
    }

    // ==========================================
    // SECURITY CONFIGURATION
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

            // ----------------------------------
            // ENABLE CORS
            // ----------------------------------
            .cors(Customizer.withDefaults())

            // ----------------------------------
            // DISABLE CSRF FOR REST API
            // ----------------------------------
            .csrf(csrf ->
                csrf.disable())

            // ----------------------------------
            // AUTHENTICATION PROVIDER
            // ----------------------------------
            .authenticationProvider(
                authenticationProvider())

            // ----------------------------------
            // AUTHORIZATION
            // All fine-grained access control is
            // enforced via @PreAuthorize on each
            // controller method. All requests here
            // only require authentication.
            // ----------------------------------
            .authorizeHttpRequests(auth ->
                auth

                    // Allow browser CORS preflight
                    .requestMatchers(
                        org.springframework.http.HttpMethod.OPTIONS,
                        "/**")
                    .permitAll()

                    // Allow public registration
                    .requestMatchers(
                        org.springframework.http.HttpMethod.POST,
                        "/api/v1/users/register")
                    .permitAll()

                    // All other requests must be authenticated
                    .anyRequest()
                    .authenticated()
            )

            // ----------------------------------
            // HTTP BASIC AUTHENTICATION
            // ----------------------------------
            .httpBasic(
                Customizer.withDefaults());

        return http.build();
    }
}