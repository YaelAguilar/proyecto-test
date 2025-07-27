package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.TypeEquipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeEquipmentRepositoryImpl implements ITypeEquipmentRepository {
    private static final Logger log = LoggerFactory.getLogger(TypeEquipmentRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public TypeEquipmentRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TypeEquipment> findById(int id) {
        String sql = "SELECT id, type FROM type_equipment WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new TypeEquipment(rs.getInt("id"), rs.getString("type")));
            }
        } catch (SQLException e) {
            log.error("Error finding type equipment by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding type equipment", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TypeEquipment> findByType(String type) {
        String sql = "SELECT id, type FROM type_equipment WHERE type = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new TypeEquipment(rs.getInt("id"), rs.getString("type")));
            }
        } catch (SQLException e) {
            log.error("Error finding type equipment by type {}: {}", type, e.getMessage(), e);
            throw new RuntimeException("Database error finding type equipment by type", e);
        }
        return Optional.empty();
    }

    @Override
    public List<TypeEquipment> findAll() {
        List<TypeEquipment> types = new ArrayList<>();
        String sql = "SELECT id, type FROM type_equipment";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                types.add(new TypeEquipment(rs.getInt("id"), rs.getString("type")));
            }
        } catch (SQLException e) {
            log.error("Error finding all type equipment", e);
            throw new RuntimeException("Database error finding all type equipment", e);
        }
        return types;
    }

    @Override
    public TypeEquipment save(TypeEquipment typeEquipment) {
        String sql = "INSERT INTO type_equipment (type) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, typeEquipment.getType());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating type equipment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    typeEquipment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating type equipment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving type equipment {}: {}", typeEquipment.getType(), e.getMessage(), e);
            throw new RuntimeException("Error saving type equipment", e);
        }
        return typeEquipment;
    }

    @Override
    public TypeEquipment update(TypeEquipment typeEquipment) {
        String sql = "UPDATE type_equipment SET type = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, typeEquipment.getType());
            stmt.setInt(2, typeEquipment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating type equipment {}: {}", typeEquipment.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating type equipment", e);
        }
        return typeEquipment;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM type_equipment WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting type equipment by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting type equipment", e);
        }
    }
}