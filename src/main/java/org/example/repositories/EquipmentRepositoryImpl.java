package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Equipment;
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

public class EquipmentRepositoryImpl implements IEquipmentRepository {
    private static final Logger log = LoggerFactory.getLogger(EquipmentRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public EquipmentRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Equipment> findById(int id) {
        String sql = "SELECT id, id_provider, id_type_equipment, name, description, price, id_state_equipment, url_image, id_brand, created_at FROM equipment WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding equipment by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding equipment", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Equipment> findAll() {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT id, id_provider, id_type_equipment, name, description, price, id_state_equipment, url_image, id_brand, created_at FROM equipment";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding all equipment", e);
            throw new RuntimeException("Database error finding all equipment", e);
        }
        return equipmentList;
    }

    @Override
    public List<Equipment> findByProviderId(int providerId) {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT id, id_provider, id_type_equipment, name, description, price, id_state_equipment, url_image, id_brand, created_at FROM equipment WHERE id_provider = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, providerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                equipmentList.add(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding equipment by provider ID {}: {}", providerId, e.getMessage(), e);
            throw new RuntimeException("Database error finding equipment by provider", e);
        }
        return equipmentList;
    }

    @Override
    public Equipment save(Equipment equipment) {
        String sql = "INSERT INTO equipment (id_provider, id_type_equipment, name, description, price, id_state_equipment, url_image, id_brand) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, equipment.getIdProvider());
            stmt.setInt(2, equipment.getIdTypeEquipment());
            stmt.setString(3, equipment.getName());
            stmt.setString(4, equipment.getDescription());
            stmt.setBigDecimal(5, equipment.getPrice());
            stmt.setInt(6, equipment.getIdStateEquipment());
            stmt.setString(7, equipment.getUrlImage());
            stmt.setInt(8, equipment.getIdBrand());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating equipment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    equipment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating equipment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving equipment {}: {}", equipment.getName(), e.getMessage(), e);
            throw new RuntimeException("Error saving equipment", e);
        }
        return equipment;
    }

    @Override
    public Equipment update(Equipment equipment) {
        String sql = "UPDATE equipment SET id_provider = ?, id_type_equipment = ?, name = ?, description = ?, price = ?, id_state_equipment = ?, url_image = ?, id_brand = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipment.getIdProvider());
            stmt.setInt(2, equipment.getIdTypeEquipment());
            stmt.setString(3, equipment.getName());
            stmt.setString(4, equipment.getDescription());
            stmt.setBigDecimal(5, equipment.getPrice());
            stmt.setInt(6, equipment.getIdStateEquipment());
            stmt.setString(7, equipment.getUrlImage());
            stmt.setInt(8, equipment.getIdBrand());
            stmt.setInt(9, equipment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating equipment {}: {}", equipment.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating equipment", e);
        }
        return equipment;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting equipment by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting equipment", e);
        }
    }

    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setId(rs.getInt("id"));
        equipment.setIdProvider(rs.getInt("id_provider"));
        equipment.setIdTypeEquipment(rs.getInt("id_type_equipment"));
        equipment.setName(rs.getString("name"));
        equipment.setDescription(rs.getString("description"));
        equipment.setPrice(rs.getBigDecimal("price"));
        equipment.setIdStateEquipment(rs.getInt("id_state_equipment"));
        equipment.setUrlImage(rs.getString("url_image"));
        equipment.setIdBrand(rs.getInt("id_brand"));
        equipment.setCreatedAt(rs.getTimestamp("created_at"));
        return equipment;
    }
}