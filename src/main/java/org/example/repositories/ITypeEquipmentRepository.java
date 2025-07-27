package org.example.repositories;

import org.example.models.TypeEquipment;

import java.util.List;
import java.util.Optional;

public interface ITypeEquipmentRepository {
    Optional<TypeEquipment> findById(int id);
    Optional<TypeEquipment> findByType(String type);
    List<TypeEquipment> findAll();
    TypeEquipment save(TypeEquipment typeEquipment);
    TypeEquipment update(TypeEquipment typeEquipment);
    void delete(int id);
}