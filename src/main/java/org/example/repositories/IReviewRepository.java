package org.example.repositories;

import org.example.models.Review;

import java.util.List;
import java.util.Optional;

public interface IReviewRepository {
    Optional<Review> findById(int id);
    Optional<Review> findByUserIdAndEquipmentId(int userId, int equipmentId); // Para evitar duplicados
    List<Review> findByEquipmentId(int equipmentId);
    List<Review> findByUserId(int userId);
    Review save(Review review);
    // Para reseñas, no suele haber actualización de contenido, solo eliminación.
    // Review update(Review review);
    void delete(int id);
}