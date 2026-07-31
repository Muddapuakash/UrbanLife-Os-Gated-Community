package com.urbanlife.dto;

import java.time.LocalDateTime;

import com.urbanlife.enums.RoleName;
import com.urbanlife.enums.UserStatus;

public class UserResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private UserStatus status;

    private Long roleId;

    private RoleName roleName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UserResponse() {
    }

    public UserResponse(
            Long userId,
            String firstName,
            String lastName,
            String email,
            String phone,
            UserStatus status,
            Long roleId,
            RoleName roleName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.roleId = roleId;
        this.roleName = roleName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Long getRoleId() {
        return roleId;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}