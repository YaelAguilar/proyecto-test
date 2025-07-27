package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Brand;
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

public class BrandRepositoryImpl implements IBrandRepository {
    private static final Logger log = LoggerFactory.getLogger(BrandRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public BrandRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Brand> findById(int id) {
        String sql = "SELECT id, name_brand FROM brands WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Brand(rs.getInt("id"), rs.getString("name_brand")));
            }
        } catch (SQLException e) {
            log.error("Error finding brand by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding brand", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Brand> findByName(String nameBrand) {
        String sql = "SELECT id, name_brand FROM brands WHERE name_brand = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nameBrand);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Brand(rs.getInt("id"), rs.getString("name_brand")));
            }
        } catch (SQLException e) {
            log.error("Error finding brand by name {}: {}", nameBrand, e.getMessage(), e);
            throw new RuntimeException("Database error finding brand by name", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Brand> findAll() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT id, name_brand FROM brands";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                brands.add(new Brand(rs.getInt("id"), rs.getString("name_brand")));
            }
        } catch (SQLException e) {
            log.error("Error finding all brands", e);
            throw new RuntimeException("Database error finding all brands", e);
        }
        return brands;
    }

    @Override
    public Brand save(Brand brand) {
        String sql = "INSERT INTO brands (name_brand) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, brand.getNameBrand());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating brand failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    brand.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating brand failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving brand {}: {}", brand.getNameBrand(), e.getMessage(), e);
            throw new RuntimeException("Error saving brand", e);
        }
        return brand;
    }

    @Override
    public Brand update(Brand brand) {
        String sql = "UPDATE brands SET name_brand = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, brand.getNameBrand());
            stmt.setInt(2, brand.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating brand {}: {}", brand.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating brand", e);
        }
        return brand;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM brands WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting brand by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting brand", e);
        }
    }
}