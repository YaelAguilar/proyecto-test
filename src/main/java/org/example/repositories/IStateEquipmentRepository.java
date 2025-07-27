package org.example.repositories;

import org.example.models.StateEquipment;

import java.util.List;
import java.util.Optional;

public interface IStateEquipmentRepository {
    Optional<StateEquipment> findById(int id);
    Optional<StateEquipment> findByState(String state);
    List<StateEquipment> findAll();
    StateEquipment save(StateEquipment stateEquipment);
    StateEquipment update(StateEquipment stateEquipment);
    void delete(int id);
}