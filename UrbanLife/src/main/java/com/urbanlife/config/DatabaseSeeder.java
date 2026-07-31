package com.urbanlife.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.urbanlife.entity.Role;
import com.urbanlife.entity.User;
import com.urbanlife.enums.RoleName;
import com.urbanlife.enums.UserStatus;
import com.urbanlife.repository.RoleRepository;
import com.urbanlife.repository.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedRoles();
        seedSuperAdmin();
        seedAdmin();
    }

    private void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = new Role();
                role.setRoleName(roleName);
                roleRepository.save(role);
                System.out.println("[DatabaseSeeder] Seeded Role: " + roleName);
            }
        }
    }

    private void seedSuperAdmin() {
        String email = "akash@gmail.com";

        if (!userRepository.existsByEmail(email)) {

            Role role = roleRepository.findByRoleName(RoleName.SUPER_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found"));

            User user = new User();
            user.setFirstName("Akash");
            user.setLastName("Muddapu");
            user.setEmail(email);
            user.setPhone("7989200103");
            user.setPassword(passwordEncoder.encode("Test@123"));
            user.setStatus(UserStatus.ACTIVE);
            user.setRole(role);

            userRepository.save(user);

            System.out.println("==============================================");
            System.out.println(" Default SUPER_ADMIN Created");
            System.out.println(" Email    : " + email);
            System.out.println(" Password : Test@123");
            System.out.println("==============================================");
        }
    }

    private void seedAdmin() {
        String email = "ram@gmail.com";

        if (!userRepository.existsByEmail(email)) {

            Role role = roleRepository.findByRoleName(RoleName.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

            User user = new User();
            user.setFirstName("Ram");
            user.setLastName("Admin");
            user.setEmail(email);
            user.setPhone("9876543210");
            user.setPassword(passwordEncoder.encode("Test@123"));
            user.setStatus(UserStatus.ACTIVE);
            user.setRole(role);

            userRepository.save(user);

            System.out.println("==============================================");
            System.out.println(" Default ADMIN Created");
            System.out.println(" Email    : " + email);
            System.out.println(" Password : Test@123");
            System.out.println("==============================================");
        }
    }
}