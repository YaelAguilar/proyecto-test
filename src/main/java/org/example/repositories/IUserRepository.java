package org.example.repositories;

import org.example.models.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User user);
    User update(User user);
    void delete(int id);
}