package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RoleRepositoryImpl implements IRoleRepository {
    private static final Logger log = LoggerFactory.getLogger(RoleRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public RoleRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Role> findById(int id) {
        String sql = "SELECT id, role, created_at FROM roles WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Role(
                        rs.getInt("id"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            log.error("Error finding role by ID: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        String sql = "SELECT id, role, created_at FROM roles WHERE role = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Role(
                        rs.getInt("id"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            log.error("Error finding role by name: {}", roleName, e);
        }
        return Optional.empty();
    }
}