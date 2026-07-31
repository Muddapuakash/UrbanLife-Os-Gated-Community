package com.urbanlife.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.urbanlife.entity.User;
import com.urbanlife.enums.UserStatus;
import com.urbanlife.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        // Find user using email
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found with email: "
                        + email));

        // Get role
        String role = user
                .getRole()
                .getRoleName()
                .name();

        // Only ACTIVE users are enabled
        boolean enabled =
                user.getStatus()
                    == UserStatus.ACTIVE;

        return org.springframework.security
                .core
                .userdetails
                .User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .disabled(!enabled)
                .build();
    }
}