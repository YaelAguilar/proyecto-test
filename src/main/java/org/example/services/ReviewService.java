package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import org.example.models.Equipment;
import org.example.models.Review;
import org.example.models.Star;
import org.example.models.User;
import org.example.models.dtos.ReviewCreateUpdateDto;
import org.example.models.dtos.ReviewResponseDto;
import org.example.repositories.IEquipmentRepository;
import org.example.repositories.IReviewRepository;
import org.example.repositories.IStarRepository;
import org.example.repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewService {
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final IReviewRepository reviewRepository;
    private final IEquipmentRepository equipmentRepository;
    private final IUserRepository userRepository;
    private final IStarRepository starRepository;

    public ReviewService(IReviewRepository reviewRepository, IEquipmentRepository equipmentRepository, IUserRepository userRepository, IStarRepository starRepository) {
        this.reviewRepository = reviewRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.starRepository = starRepository;
    }

    public Review createReview(ReviewCreateUpdateDto reviewDto, int authUserId) {
        // 1. Validar DTO
        if (reviewDto.getEquipmentId() <= 0 || reviewDto.getStarValue() < 1 || reviewDto.getStarValue() > 5) {
            throw new BadRequestResponse("ID de equipo válido y valor de estrella (1-5) son obligatorios.");
        }

        // 2. Verificar que el usuario existe
        userRepository.findById(authUserId)
                .orElseThrow(() -> new NotFoundResponse("Usuario autenticado no encontrado."));

        // 3. Verificar que el equipo existe
        equipmentRepository.findById(reviewDto.getEquipmentId())
                .orElseThrow(() -> new NotFoundResponse("Equipo no encontrado con ID: " + reviewDto.getEquipmentId()));

        // 4. Obtener ID de la estrella por su valor
        Star star = starRepository.findByValue(reviewDto.getStarValue())
                .orElseThrow(() -> new BadRequestResponse("Valor de estrella inválido. Debe ser entre 1 y 5."));

        // 5. Verificar si el usuario ya ha dejado una reseña para este equipo
        if (reviewRepository.findByUserIdAndEquipmentId(authUserId, reviewDto.getEquipmentId()).isPresent()) {
            throw new BadRequestResponse("Ya has dejado una reseña para este equipo.");
        }

        // 6. Crear y guardar la reseña
        Review newReview = new Review();
        newReview.setIdEquipment(reviewDto.getEquipmentId());
        newReview.setIdUser(authUserId);
        newReview.setIdStar(star.getId());
        newReview.setBody(reviewDto.getBody());

        log.info("Creando reseña para equipo {} por usuario {}", reviewDto.getEquipmentId(), authUserId);
        return reviewRepository.save(newReview);
    }

    public List<ReviewResponseDto> getReviewsForEquipment(int equipmentId) {
        equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundResponse("Equipo no encontrado con ID: " + equipmentId));

        List<Review> reviews = reviewRepository.findByEquipmentId(equipmentId);
        log.info("Obteniendo {} reseñas para el equipo ID: {}", reviews.size(), equipmentId);

        return reviews.stream()
                .map(this::mapReviewToDto)
                .collect(Collectors.toList());
    }

    public List<ReviewResponseDto> getReviewsByUserId(int userId, int authUserId) {
        // Asegurar que el usuario solo puede ver sus propias reseñas
        if (userId != authUserId) {
            throw new ForbiddenResponse("No tienes permiso para ver las reseñas de otros usuarios.");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundResponse("Usuario no encontrado con ID: " + userId));

        List<Review> reviews = reviewRepository.findByUserId(userId);
        log.info("Obteniendo {} reseñas para el usuario ID: {}", reviews.size(), userId);
        return reviews.stream()
                .map(this::mapReviewToDto)
                .collect(Collectors.toList());
    }

    public void deleteReview(int reviewId, int authUserId) {
        // 1. Buscar la reseña
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundResponse("Reseña no encontrada con ID: " + reviewId));

        // 2. Verificar que el usuario autenticado es el dueño de la reseña
        if (review.getIdUser() != authUserId) {
            throw new ForbiddenResponse("No tienes permiso para eliminar esta reseña.");
        }

        log.info("Eliminando reseña ID {} del usuario {}", reviewId, authUserId);
        reviewRepository.delete(reviewId);
    }

    // Método helper para mapear Review a ReviewResponseDto
    private ReviewResponseDto mapReviewToDto(Review review) {
        String userName = userRepository.findById(review.getIdUser())
                .map(User::getFullName)
                .orElse("Usuario Desconocido");
        String equipmentName = equipmentRepository.findById(review.getIdEquipment())
                .map(Equipment::getName)
                .orElse("Equipo Desconocido");
        int starValue = starRepository.findById(review.getIdStar())
                .map(Star::getValue)
                .orElse(0); // 0 o lanza excepción si no encuentra el valor de la estrella

        return new ReviewResponseDto(
                review.getId(),
                review.getIdEquipment(),
                equipmentName,
                review.getIdUser(),
                userName,
                starValue,
                review.getBody(),
                review.getCreatedAt()
        );
    }
}