package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.Favorite;
import org.example.models.dtos.FavoriteRequestDto;
import org.example.services.FavoriteService;
import org.example.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FavoriteController {
    private static final Logger log = LoggerFactory.getLogger(FavoriteController.class);
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public void addFavorite(Context ctx) {
        FavoriteRequestDto favoriteDto = ctx.bodyAsClass(FavoriteRequestDto.class);
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para añadir equipo {} a favoritos del usuario {}", favoriteDto.getEquipmentId(), authUserId);
        Favorite newFavorite = favoriteService.addFavorite(favoriteDto, authUserId);
        ctx.status(201).json(newFavorite);
    }

    public void getFavoritesByUser(Context ctx) {
        int userId = ctx.pathParamAsClass("id", Integer.class).get(); // El ID en la ruta
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx); // El ID del usuario autenticado
        log.info("Solicitud para obtener favoritos del usuario ID: {}", userId);
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId, authUserId);
        ctx.json(favorites);
    }

    public void removeFavorite(Context ctx) {
        int favoriteId = ctx.pathParamAsClass("id", Integer.class).get(); // El ID del favorito
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para eliminar favorito ID: {} por usuario ID: {}", favoriteId, authUserId);
        favoriteService.removeFavorite(favoriteId, authUserId);
        ctx.status(204); // No Content
    }
}