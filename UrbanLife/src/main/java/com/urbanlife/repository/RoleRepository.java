package com.urbanlife.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbanlife.entity.Role;
import com.urbanlife.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);

    boolean existsByRoleName(RoleName roleName);
}