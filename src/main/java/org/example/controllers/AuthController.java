package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.dtos.AuthResponseDto;
import org.example.models.dtos.UserLoginDto;
import org.example.models.dtos.UserRegisterDto;
import org.example.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void register(Context ctx) {
        UserRegisterDto registerDto = ctx.bodyAsClass(UserRegisterDto.class);
        log.info("Recibida solicitud de registro para email: {}", registerDto.getEmail());
        AuthResponseDto response = authService.registerUser(registerDto);
        ctx.status(201).json(response); // 201 Created
    }

    public void login(Context ctx) {
        UserLoginDto loginDto = ctx.bodyAsClass(UserLoginDto.class);
        log.info("Recibida solicitud de login para email: {}", loginDto.getEmail());
        AuthResponseDto response = authService.authenticateUser(loginDto);
        ctx.status(200).json(response); // 200 OK
    }
}