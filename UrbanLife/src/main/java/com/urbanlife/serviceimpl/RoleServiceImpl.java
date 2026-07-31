package com.urbanlife.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.urbanlife.dto.RoleDto;
import com.urbanlife.entity.Role;
import com.urbanlife.exception.DuplicateResourceException;
import com.urbanlife.exception.ResourceNotFoundException;
import com.urbanlife.repository.RoleRepository;
import com.urbanlife.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {

        if (roleRepository.existsByRoleName(roleDto.getRoleName())) {
            throw new DuplicateResourceException(
                    "Role already exists: " + roleDto.getRoleName());
        }

        Role role = new Role();
        role.setRoleName(roleDto.getRoleName());

        Role savedRole = roleRepository.save(role);

        return mapToDto(savedRole);
    }

    @Override
    public RoleDto getRoleById(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id: " + roleId));

        return mapToDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public RoleDto updateRole(Long roleId, RoleDto roleDto) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id: " + roleId));

        if (role.getRoleName() != roleDto.getRoleName()
                && roleRepository.existsByRoleName(roleDto.getRoleName())) {

            throw new DuplicateResourceException(
                    "Role already exists: " + roleDto.getRoleName());
        }

        role.setRoleName(roleDto.getRoleName());

        Role updatedRole = roleRepository.save(role);

        return mapToDto(updatedRole);
    }

    @Override
    public void deleteRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found with id: " + roleId));

        roleRepository.delete(role);
    }

    private RoleDto mapToDto(Role role) {

        return new RoleDto(
                role.getRoleId(),
                role.getRoleName()
        );
    }
}