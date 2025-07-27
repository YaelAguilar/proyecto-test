package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.Provider; // Importar Provider
import org.example.models.dtos.UserResponseDto;
import org.example.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void getUserById(Context ctx) {
        int userId = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener usuario con ID: {}", userId);
        UserResponseDto userDto = userService.getUserById(userId);
        ctx.json(userDto);
    }

    // Nuevo método para obtener el proveedor por ID de usuario
    public void getProviderByUserId(Context ctx) {
        int userId = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener proveedor del usuario con ID: {}", userId);
        Provider provider = userService.getProviderByUserId(userId);
        ctx.json(provider); // Devuelve el objeto Provider directamente
    }
}