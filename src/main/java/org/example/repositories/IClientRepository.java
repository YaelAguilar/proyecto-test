package org.example.repositories;

import org.example.models.Client;

import java.util.Optional;

public interface IClientRepository {
    Optional<Client> findByUserId(int userId);
    Client save(int userId); // Guarda un nuevo cliente, usando un ID de usuario existente
    void deleteByUserId(int userId);
}