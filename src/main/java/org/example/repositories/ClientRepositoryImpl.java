package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ClientRepositoryImpl implements IClientRepository {
    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public ClientRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Client> findByUserId(int userId) {
        String sql = "SELECT id, id_user FROM clients WHERE id_user = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Client(rs.getInt("id"), rs.getInt("id_user")));
            }
        } catch (SQLException e) {
            log.error("Error finding client by user ID: {}", userId, e);
            throw new RuntimeException("Database error finding client", e);
        }
        return Optional.empty();
    }

    @Override
    public Client save(int userId) {
        String sql = "INSERT INTO clients (id_user) VALUES (?)";
        Client client = new Client();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                    client.setIdUser(userId);
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving client for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error saving client", e);
        }
        return client;
    }

    @Override
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM clients WHERE id_user = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting client by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error deleting client", e);
        }
    }
}