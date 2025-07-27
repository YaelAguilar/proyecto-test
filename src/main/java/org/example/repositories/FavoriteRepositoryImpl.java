package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Favorite;
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

public class FavoriteRepositoryImpl implements IFavoriteRepository {
    private static final Logger log = LoggerFactory.getLogger(FavoriteRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public FavoriteRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Favorite> findById(int id) {
        String sql = "SELECT id, id_user, id_equipment, created_at FROM favorites WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToFavorite(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding favorite by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding favorite", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Favorite> findByUserIdAndEquipmentId(int userId, int equipmentId) {
        String sql = "SELECT id, id_user, id_equipment, created_at FROM favorites WHERE id_user = ? AND id_equipment = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, equipmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToFavorite(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding favorite by user ID {} and equipment ID {}: {}", userId, equipmentId, e.getMessage(), e);
            throw new RuntimeException("Database error finding favorite", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Favorite> findByUserId(int userId) {
        List<Favorite> favorites = new ArrayList<>();
        String sql = "SELECT id, id_user, id_equipment, created_at FROM favorites WHERE id_user = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                favorites.add(mapResultSetToFavorite(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding favorites by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Database error finding favorites by user", e);
        }
        return favorites;
    }

    @Override
    public Favorite save(Favorite favorite) {
        String sql = "INSERT INTO favorites (id_user, id_equipment) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, favorite.getIdUser());
            stmt.setInt(2, favorite.getIdEquipment());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating favorite failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    favorite.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating favorite failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving favorite for user {} and equipment {}: {}", favorite.getIdUser(), favorite.getIdEquipment(), e.getMessage(), e);
            throw new RuntimeException("Error saving favorite", e);
        }
        return favorite;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM favorites WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting favorite by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting favorite", e);
        }
    }

    private Favorite mapResultSetToFavorite(ResultSet rs) throws SQLException {
        Favorite favorite = new Favorite();
        favorite.setId(rs.getInt("id"));
        favorite.setIdUser(rs.getInt("id_user"));
        favorite.setIdEquipment(rs.getInt("id_equipment"));
        favorite.setCreatedAt(rs.getTimestamp("created_at"));
        return favorite;
    }
}