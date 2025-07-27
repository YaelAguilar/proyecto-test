package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Star;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StarRepositoryImpl implements IStarRepository {
    private static final Logger log = LoggerFactory.getLogger(StarRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public StarRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Star> findById(int id) {
        String sql = "SELECT id, value FROM stars WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Star(rs.getInt("id"), rs.getInt("value")));
            }
        } catch (SQLException e) {
            log.error("Error finding star by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding star", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Star> findByValue(int value) {
        String sql = "SELECT id, value FROM stars WHERE value = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, value);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Star(rs.getInt("id"), rs.getInt("value")));
            }
        } catch (SQLException e) {
            log.error("Error finding star by value {}: {}", value, e.getMessage(), e);
            throw new RuntimeException("Database error finding star by value", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Star> findAll() {
        List<Star> stars = new ArrayList<>();
        String sql = "SELECT id, value FROM stars";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stars.add(new Star(rs.getInt("id"), rs.getInt("value")));
            }
        } catch (SQLException e) {
            log.error("Error finding all stars", e);
            throw new RuntimeException("Database error finding all stars", e);
        }
        return stars;
    }
}