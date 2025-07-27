package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Review;
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

public class ReviewRepositoryImpl implements IReviewRepository {
    private static final Logger log = LoggerFactory.getLogger(ReviewRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public ReviewRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Review> findById(int id) {
        String sql = "SELECT id, id_equipment, id_user, id_star, body, created_at FROM reviews WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding review by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding review", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Review> findByUserIdAndEquipmentId(int userId, int equipmentId) {
        String sql = "SELECT id, id_equipment, id_user, id_star, body, created_at FROM reviews WHERE id_user = ? AND id_equipment = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, equipmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding review by user ID {} and equipment ID {}: {}", userId, equipmentId, e.getMessage(), e);
            throw new RuntimeException("Database error finding review", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Review> findByEquipmentId(int equipmentId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT id, id_equipment, id_user, id_star, body, created_at FROM reviews WHERE id_equipment = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding reviews by equipment ID {}: {}", equipmentId, e.getMessage(), e);
            throw new RuntimeException("Database error finding reviews by equipment", e);
        }
        return reviews;
    }

    @Override
    public List<Review> findByUserId(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT id, id_equipment, id_user, id_star, body, created_at FROM reviews WHERE id_user = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding reviews by user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Database error finding reviews by user", e);
        }
        return reviews;
    }

    @Override
    public Review save(Review review) {
        String sql = "INSERT INTO reviews (id_equipment, id_user, id_star, body) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, review.getIdEquipment());
            stmt.setInt(2, review.getIdUser());
            stmt.setInt(3, review.getIdStar());
            stmt.setString(4, review.getBody());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating review failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving review for equipment {} and user {}: {}", review.getIdEquipment(), review.getIdUser(), e.getMessage(), e);
            throw new RuntimeException("Error saving review", e);
        }
        return review;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting review by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting review", e);
        }
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setIdEquipment(rs.getInt("id_equipment"));
        review.setIdUser(rs.getInt("id_user"));
        review.setIdStar(rs.getInt("id_star"));
        review.setBody(rs.getString("body"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        return review;
    }
}