package org.example.repositories;

import org.example.models.Role;

import java.util.Optional;

public interface IRoleRepository {
    Optional<Role> findById(int id);
    Optional<Role> findByRoleName(String roleName);
}