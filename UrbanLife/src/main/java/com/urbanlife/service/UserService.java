package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.CreateUserRequest;
import com.urbanlife.dto.UpdateUserRequest;
import com.urbanlife.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long userId);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(
            Long userId,
            UpdateUserRequest request);

    void deleteUser(Long userId);
}