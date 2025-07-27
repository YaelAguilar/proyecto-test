package org.example.repositories;

import org.example.models.Star;

import java.util.List;
import java.util.Optional;

public interface IStarRepository {
    Optional<Star> findById(int id);
    Optional<Star> findByValue(int value);
    List<Star> findAll();
    // Métodos para crear/actualizar/eliminar no son necesarios si las estrellas son estáticas (1-5)
    // Star save(Star star);
    // Star update(Star star);
    // void delete(int id);
}