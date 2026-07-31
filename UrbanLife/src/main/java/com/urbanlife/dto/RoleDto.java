package com.urbanlife.dto;

import com.urbanlife.enums.RoleName;

import jakarta.validation.constraints.NotNull;

public class RoleDto {

    private Long roleId;

    @NotNull(message = "Role name is required")
    private RoleName roleName;

    public RoleDto() {
    }

    public RoleDto(Long roleId, RoleName roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }
}