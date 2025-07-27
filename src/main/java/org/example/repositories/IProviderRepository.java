package org.example.repositories;

import org.example.models.Provider;

import java.util.Optional;

public interface IProviderRepository {
    Optional<Provider> findByUserId(int userId);
    Optional<Provider> findById(int id);
    Provider save(int userId, Integer companyId); //companyId puede ser null inicialmente
    Provider update(Provider provider); // Para actualizar, ej., la compañía asociada
    void deleteByUserId(int userId);
}