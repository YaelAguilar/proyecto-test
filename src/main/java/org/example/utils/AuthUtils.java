package org.example.utils;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;

public class AuthUtils {

    public static String getTokenFromHeader(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new UnauthorizedResponse("Token de autenticación requerido.");
    }

    public static void requireRole(Context ctx, String requiredRole) {
        String token = getTokenFromHeader(ctx);
        String jwtSecret = System.getProperty("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new InternalServerErrorResponse("JWT Secret no configurado en el servidor.");
        }
        String userRole = new JwtManager(jwtSecret).getUserRoleFromToken(token);

        if (userRole == null) {
            throw new UnauthorizedResponse("Token inválido o expirado.");
        }
        if (!userRole.equalsIgnoreCase(requiredRole)) {
            throw new ForbiddenResponse("Acceso denegado: se requiere rol de " + requiredRole + ".");
        }
    }

    // NUEVO: Método para verificar múltiples roles permitidos
    public static void requireAnyRole(Context ctx, String... allowedRoles) {
        String token = getTokenFromHeader(ctx);
        String jwtSecret = System.getProperty("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new InternalServerErrorResponse("JWT Secret no configurado en el servidor.");
        }
        String userRole = new JwtManager(jwtSecret).getUserRoleFromToken(token);

        if (userRole == null) {
            throw new UnauthorizedResponse("Token inválido o expirado.");
        }
        
        boolean hasPermission = false;
        for (String allowedRole : allowedRoles) {
            if (userRole.equalsIgnoreCase(allowedRole)) {
                hasPermission = true;
                break;
            }
        }
        
        if (!hasPermission) {
            throw new ForbiddenResponse("Acceso denegado: rol insuficiente.");
        }
    }

    public static void requireOwnership(Context ctx, int resourceOwnerId) {
        String token = getTokenFromHeader(ctx);
        String jwtSecret = System.getProperty("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new InternalServerErrorResponse("JWT Secret no configurado en el servidor.");
        }
        
        // MODIFICADO: Los administradores pueden acceder a cualquier recurso
        String userRole = new JwtManager(jwtSecret).getUserRoleFromToken(token);
        if (userRole != null && userRole.equalsIgnoreCase("administrador")) {
            return; // Los administradores tienen acceso completo
        }
        
        Integer userId = new JwtManager(jwtSecret).getUserIdFromToken(token);
        if (userId == null) {
            throw new UnauthorizedResponse("Token inválido o expirado.");
        }
        if (userId != resourceOwnerId) {
            throw new ForbiddenResponse("Acceso denegado: no es el propietario del recurso.");
        }
    }

    public static int getAuthenticatedUserId(Context ctx) {
        String token = getTokenFromHeader(ctx);
        String jwtSecret = System.getProperty("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new InternalServerErrorResponse("JWT Secret no configurado en el servidor.");
        }
        Integer userId = new JwtManager(jwtSecret).getUserIdFromToken(token);
        if (userId == null) {
            throw new UnauthorizedResponse("No se pudo obtener el ID del usuario autenticado.");
        }
        return userId;
    }

    public static String getAuthenticatedUserRole(Context ctx) {
        String token = getTokenFromHeader(ctx);
        String jwtSecret = System.getProperty("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new InternalServerErrorResponse("JWT Secret no configurado en el servidor.");
        }
        String userRole = new JwtManager(jwtSecret).getUserRoleFromToken(token);
        if (userRole == null) {
            throw new UnauthorizedResponse("No se pudo obtener el rol del usuario autenticado.");
        }
        return userRole;
    }
}