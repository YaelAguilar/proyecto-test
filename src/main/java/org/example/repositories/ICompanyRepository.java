package org.example.repositories;

import org.example.models.Company;

import java.util.List; // <-- CORREGIDO: Importación de List
import java.util.Optional;

public interface ICompanyRepository {
    Optional<Company> findById(int id);
    Optional<Company> findByName(String name);
    List<Company> findAll(); // <-- CORREGIDO: Añadido el método findAll
    Company save(Company company);
    Company update(Company company);
    void delete(int id);
}