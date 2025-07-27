package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ProviderRepositoryImpl implements IProviderRepository {
    private static final Logger log = LoggerFactory.getLogger(ProviderRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public ProviderRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Provider> findByUserId(int userId) {
        String sql = "SELECT id, id_user, id_company FROM providers WHERE id_user = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Provider(rs.getInt("id"), rs.getInt("id_user"), rs.getInt("id_company")));
            }
        } catch (SQLException e) {
            log.error("Error finding provider by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Database error finding provider", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Provider> findById(int id) {
        String sql = "SELECT id, id_user, id_company FROM providers WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Provider(rs.getInt("id"), rs.getInt("id_user"), rs.getInt("id_company")));
            }
        } catch (SQLException e) {
            log.error("Error finding provider by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding provider", e);
        }
        return Optional.empty();
    }

    @Override
    public Provider save(int userId, Integer companyId) {
        String sql = "INSERT INTO providers (id_user, id_company) VALUES (?, ?)";
        Provider provider = new Provider();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            if (companyId != null) {
                stmt.setInt(2, companyId);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating provider failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    provider.setId(generatedKeys.getInt(1));
                    provider.setIdUser(userId);
                    provider.setIdCompany(companyId != null ? companyId : 0); // O manejar como null en el POJO si es preferible
                } else {
                    throw new SQLException("Creating provider failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving provider for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error saving provider", e);
        }
        return provider;
    }

    @Override
    public Provider update(Provider provider) {
        String sql = "UPDATE providers SET id_user = ?, id_company = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, provider.getIdUser());
            if (provider.getIdCompany() != 0) { // Asumiendo 0 significa null o no asignado
                stmt.setInt(2, provider.getIdCompany());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, provider.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating provider {}: {}", provider.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating provider", e);
        }
        return provider;
    }

    @Override
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM providers WHERE id_user = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting provider by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error deleting provider", e);
        }
    }
}