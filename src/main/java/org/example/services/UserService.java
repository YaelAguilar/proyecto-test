package org.example.services;

import io.javalin.http.NotFoundResponse;
import io.javalin.http.InternalServerErrorResponse; // <-- CORREGIDO: Importación de InternalServerErrorResponse
import org.example.models.Provider;
import org.example.models.Role;
import org.example.models.User;
import org.example.models.dtos.UserResponseDto;
import org.example.repositories.IProviderRepository;
import org.example.repositories.IRoleRepository;
import org.example.repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IProviderRepository providerRepository;

    public UserService(IUserRepository userRepository, IRoleRepository roleRepository, IProviderRepository providerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
    }

    public UserResponseDto getUserById(int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundResponse("Usuario no encontrado con ID: " + userId);
        }
        User user = userOpt.get();

        Optional<Role> roleOpt = roleRepository.findById(user.getIdRole());
        String roleName = roleOpt.map(Role::getRole).orElse("Desconocido");

        log.info("Obteniendo información del usuario ID: {}", userId);
        return new UserResponseDto(user.getId(), user.getFullName(), user.getEmail(), roleName, user.getCreatedAt());
    }

    // Nuevo método para obtener el proveedor asociado a un usuario
    public Provider getProviderByUserId(int userId) {
        // Primero, verificar que el usuario existe y es un proveedor (opcional pero buena práctica)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundResponse("Usuario no encontrado con ID: " + userId));

        Role userRole = roleRepository.findById(user.getIdRole())
                .orElseThrow(() -> new InternalServerErrorResponse("Rol de usuario no encontrado para ID: " + userId)); // <-- CORREGIDO: la lambda estaba bien, solo faltaba el import.

        if (!userRole.getRole().equalsIgnoreCase("proveedor")) {
            throw new NotFoundResponse("El usuario con ID " + userId + " no es un proveedor.");
        }

        // Luego, obtener el registro de proveedor
        return providerRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundResponse("Proveedor no encontrado para el usuario con ID: " + userId));
    }
}