package org.example.repositories;

import org.example.models.Equipment;

import java.util.List;
import java.util.Optional;

public interface IEquipmentRepository {
    Optional<Equipment> findById(int id);
    List<Equipment> findAll();
    List<Equipment> findByProviderId(int providerId);
    Equipment save(Equipment equipment);
    Equipment update(Equipment equipment);
    void delete(int id);
}