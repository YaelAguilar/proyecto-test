package org.example.repositories;

import org.example.models.Brand;

import java.util.List;
import java.util.Optional;

public interface IBrandRepository {
    Optional<Brand> findById(int id);
    Optional<Brand> findByName(String nameBrand);
    List<Brand> findAll();
    Brand save(Brand brand);
    Brand update(Brand brand);
    void delete(int id);
}