package com.urbanlife.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbanlife.dto.RoleDto;
import com.urbanlife.service.RoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
@Validated
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // =====================================================
    // CREATE ROLE
    // SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<RoleDto> createRole(
            @Valid @RequestBody RoleDto roleDto) {

        return new ResponseEntity<>(
                roleService.createRole(roleDto),
                HttpStatus.CREATED
        );
    }

    // =====================================================
    // GET ROLE BY ID
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDto> getRoleById(
            @PathVariable Long roleId) {

        return ResponseEntity.ok(
                roleService.getRoleById(roleId)
        );
    }

    // =====================================================
    // GET ALL ROLES
    // SUPER ADMIN + ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {

        return ResponseEntity.ok(
                roleService.getAllRoles()
        );
    }

    // =====================================================
    // UPDATE ROLE
    // SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleDto roleDto) {

        return ResponseEntity.ok(
                roleService.updateRole(roleId, roleDto)
        );
    }

    // =====================================================
    // DELETE ROLE
    // SUPER ADMIN ONLY
    // =====================================================

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable Long roleId) {

        roleService.deleteRole(roleId);

        return ResponseEntity.noContent().build();
    }
}