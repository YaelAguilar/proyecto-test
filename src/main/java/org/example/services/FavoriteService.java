package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import org.example.models.Favorite;
import org.example.models.dtos.FavoriteRequestDto;
import org.example.repositories.IEquipmentRepository;
import org.example.repositories.IFavoriteRepository;
import org.example.repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FavoriteService {
    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);
    private final IFavoriteRepository favoriteRepository;
    private final IEquipmentRepository equipmentRepository;
    private final IUserRepository userRepository;

    public FavoriteService(IFavoriteRepository favoriteRepository, IEquipmentRepository equipmentRepository, IUserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
    }

    public Favorite addFavorite(FavoriteRequestDto favoriteDto, int authUserId) {
        // 1. Validar DTO
        if (favoriteDto.getEquipmentId() <= 0) {
            throw new BadRequestResponse("El ID del equipo es obligatorio.");
        }

        // 2. Verificar que el usuario existe (aunque venga del token, es buena práctica)
        userRepository.findById(authUserId)
                .orElseThrow(() -> new NotFoundResponse("Usuario autenticado no encontrado."));

        // 3. Verificar que el equipo existe
        equipmentRepository.findById(favoriteDto.getEquipmentId())
                .orElseThrow(() -> new NotFoundResponse("Equipo no encontrado con ID: " + favoriteDto.getEquipmentId()));

        // 4. Verificar si ya es favorito para este usuario
        if (favoriteRepository.findByUserIdAndEquipmentId(authUserId, favoriteDto.getEquipmentId()).isPresent()) {
            throw new BadRequestResponse("Este equipo ya está en tus favoritos.");
        }

        // 5. Crear y guardar el favorito
        Favorite newFavorite = new Favorite();
        newFavorite.setIdUser(authUserId);
        newFavorite.setIdEquipment(favoriteDto.getEquipmentId());

        log.info("Añadiendo equipo {} a favoritos del usuario {}", favoriteDto.getEquipmentId(), authUserId);
        return favoriteRepository.save(newFavorite);
    }

    public List<Favorite> getFavoritesByUserId(int userId, int authUserId) {
        // Asegurar que el usuario solo puede ver sus propios favoritos
        if (userId != authUserId) {
            throw new ForbiddenResponse("No tienes permiso para ver los favoritos de otros usuarios.");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundResponse("Usuario no encontrado con ID: " + userId));

        log.info("Obteniendo favoritos para el usuario ID: {}", userId);
        return favoriteRepository.findByUserId(userId);
    }

    public void removeFavorite(int favoriteId, int authUserId) {
        // 1. Buscar el favorito por ID
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NotFoundResponse("Favorito no encontrado con ID: " + favoriteId));

        // 2. Verificar que el usuario autenticado es el dueño del favorito
        if (favorite.getIdUser() != authUserId) {
            throw new ForbiddenResponse("No tienes permiso para eliminar este favorito.");
        }

        log.info("Eliminando favorito ID {} del usuario {}", favoriteId, authUserId);
        favoriteRepository.delete(favoriteId);
    }
}