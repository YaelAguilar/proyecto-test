package org.example.repositories;

import org.example.models.Favorite;

import java.util.List;
import java.util.Optional;

public interface IFavoriteRepository {
    Optional<Favorite> findById(int id);
    Optional<Favorite> findByUserIdAndEquipmentId(int userId, int equipmentId);
    List<Favorite> findByUserId(int userId);
    Favorite save(Favorite favorite);
    void delete(int id);
}