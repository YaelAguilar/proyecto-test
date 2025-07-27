package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.Review;
import org.example.models.dtos.ReviewCreateUpdateDto;
import org.example.models.dtos.ReviewResponseDto;
import org.example.services.ReviewService;
import org.example.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReviewController {
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public void createReview(Context ctx) {
        ReviewCreateUpdateDto reviewDto = ctx.bodyAsClass(ReviewCreateUpdateDto.class);
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para crear reseña para equipo {} por usuario {}", reviewDto.getEquipmentId(), authUserId);
        Review newReview = reviewService.createReview(reviewDto, authUserId);
        ctx.status(201).json(newReview);
    }

    public void getReviewsForEquipment(Context ctx) {
        int equipmentId = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener reseñas del equipo ID: {}", equipmentId);
        List<ReviewResponseDto> reviews = reviewService.getReviewsForEquipment(equipmentId);
        ctx.json(reviews);
    }

    public void getReviewsByUser(Context ctx) {
        int userId = ctx.pathParamAsClass("id", Integer.class).get(); // El ID en la ruta
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx); // El ID del usuario autenticado
        log.info("Solicitud para obtener reseñas del usuario ID: {}", userId);
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUserId(userId, authUserId);
        ctx.json(reviews);
    }

    public void deleteReview(Context ctx) {
        int reviewId = ctx.pathParamAsClass("id", Integer.class).get(); // El ID de la reseña
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para eliminar reseña ID: {} por usuario ID: {}", reviewId, authUserId);
        reviewService.deleteReview(reviewId, authUserId);
        ctx.status(204); // No Content
    }
}