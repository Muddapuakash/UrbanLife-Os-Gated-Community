package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.urbanlife.dto.CreateUserRequest;
import com.urbanlife.dto.UpdateUserRequest;
import com.urbanlife.dto.UserResponse;
import com.urbanlife.entity.Role;
import com.urbanlife.entity.User;
import com.urbanlife.enums.UserStatus;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.RoleRepository;
import com.urbanlife.repository.UserRepository;
import com.urbanlife.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // CREATE USER
    // =====================================================

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already registered: "
                            + request.getEmail());
        }

        // Check duplicate phone
        if (userRepository.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone number already registered: "
                            + request.getPhone());
        }

        // Find role
        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id: "
                                        + request.getRoleId()));

        User user = new User();

        user.setFirstName(
                request.getFirstName());

        user.setLastName(
                request.getLastName());

        user.setEmail(
                request.getEmail());

        user.setPhone(
                request.getPhone());

        // IMPORTANT:
        // Encode password using BCrypt before saving
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setStatus(
                UserStatus.ACTIVE);

        user.setRole(role);

        User savedUser =
                userRepository.save(user);

        return mapToResponse(savedUser);
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @Override
    public UserResponse getUserById(
            Long userId) {

        User user =
                findUser(userId);

        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToResponse(user);
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE USER
    // =====================================================

    @Override
    public UserResponse updateUser(
            Long userId,
            UpdateUserRequest request) {

        User user =
                findUser(userId);

        // Check duplicate email
        if (!user.getEmail()
                .equals(request.getEmail())
                && userRepository
                    .existsByEmail(
                        request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already registered: "
                            + request.getEmail());
        }

        // Check duplicate phone
        if (!user.getPhone()
                .equals(request.getPhone())
                && userRepository
                    .existsByPhone(
                        request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone number already registered: "
                            + request.getPhone());
        }

        // Find role
        Role role =
                roleRepository
                    .findById(
                        request.getRoleId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Role not found with id: "
                            + request.getRoleId()));

        user.setFirstName(
                request.getFirstName());

        user.setLastName(
                request.getLastName());

        user.setEmail(
                request.getEmail());

        user.setPhone(
                request.getPhone());

        user.setStatus(
                request.getStatus());

        user.setRole(role);

        /*
         * Password is NOT changed here.
         *
         * UpdateUserRequest currently does not appear
         * to contain password handling.
         *
         * Existing BCrypt password remains unchanged.
         */

        User updatedUser =
                userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    // =====================================================
    // DELETE USER
    // =====================================================

    @Override
    public void deleteUser(
            Long userId) {

        User user =
                findUser(userId);

        userRepository.delete(user);
    }

    // =====================================================
    // FIND USER
    // =====================================================

    private User findUser(
            Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: "
                        + userId));
    }

    // =====================================================
    // ENTITY -> RESPONSE DTO
    // =====================================================

    private UserResponse mapToResponse(
            User user) {

        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getRole().getRoleId(),
                user.getRole().getRoleName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}