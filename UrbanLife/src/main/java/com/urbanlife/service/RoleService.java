package com.urbanlife.service;

import java.util.List;

import com.urbanlife.dto.RoleDto;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto);

    RoleDto getRoleById(Long roleId);

    List<RoleDto> getAllRoles();

    RoleDto updateRole(Long roleId, RoleDto roleDto);

    void deleteRole(Long roleId);
}