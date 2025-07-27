package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.StateEquipment;
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

public class StateEquipmentRepositoryImpl implements IStateEquipmentRepository {
    private static final Logger log = LoggerFactory.getLogger(StateEquipmentRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public StateEquipmentRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<StateEquipment> findById(int id) {
        String sql = "SELECT id, state FROM state_equipment WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new StateEquipment(rs.getInt("id"), rs.getString("state")));
            }
        } catch (SQLException e) {
            log.error("Error finding state equipment by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding state equipment", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<StateEquipment> findByState(String state) {
        String sql = "SELECT id, state FROM state_equipment WHERE state = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, state);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new StateEquipment(rs.getInt("id"), rs.getString("state")));
            }
        } catch (SQLException e) {
            log.error("Error finding state equipment by state {}: {}", state, e.getMessage(), e);
            throw new RuntimeException("Database error finding state equipment by state", e);
        }
        return Optional.empty();
    }

    @Override
    public List<StateEquipment> findAll() {
        List<StateEquipment> states = new ArrayList<>();
        String sql = "SELECT id, state FROM state_equipment";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                states.add(new StateEquipment(rs.getInt("id"), rs.getString("state")));
            }
        } catch (SQLException e) {
            log.error("Error finding all state equipment", e);
            throw new RuntimeException("Database error finding all state equipment", e);
        }
        return states;
    }

    @Override
    public StateEquipment save(StateEquipment stateEquipment) {
        String sql = "INSERT INTO state_equipment (state) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, stateEquipment.getState());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating state equipment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stateEquipment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating state equipment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving state equipment {}: {}", stateEquipment.getState(), e.getMessage(), e);
            throw new RuntimeException("Error saving state equipment", e);
        }
        return stateEquipment;
    }

    @Override
    public StateEquipment update(StateEquipment stateEquipment) {
        String sql = "UPDATE state_equipment SET state = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stateEquipment.getState());
            stmt.setInt(2, stateEquipment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating state equipment {}: {}", stateEquipment.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating state equipment", e);
        }
        return stateEquipment;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM state_equipment WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting state equipment by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting state equipment", e);
        }
    }
}