package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.CompanyBrand;
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

public class CompanyBrandRepositoryImpl implements ICompanyBrandRepository {
    private static final Logger log = LoggerFactory.getLogger(CompanyBrandRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public CompanyBrandRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CompanyBrand save(int companyId, int brandId) {
        String sql = "INSERT INTO company_brands (id_company, id_brand) VALUES (?, ?)";
        CompanyBrand companyBrand = new CompanyBrand();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, brandId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating company-brand association failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    companyBrand.setId(generatedKeys.getInt(1));
                    companyBrand.setIdCompany(companyId);
                    companyBrand.setIdBrand(brandId);
                } else {
                    throw new SQLException("Creating company-brand association failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving company-brand association (companyId: {}, brandId: {}): {}", companyId, brandId, e.getMessage(), e);
            throw new RuntimeException("Error saving company-brand association", e);
        }
        return companyBrand;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM company_brands WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting company-brand association by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting company-brand association", e);
        }
    }

    @Override
    public void deleteByCompanyAndBrand(int companyId, int brandId) {
        String sql = "DELETE FROM company_brands WHERE id_company = ? AND id_brand = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, brandId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting company-brand association (companyId: {}, brandId: {}): {}", companyId, brandId, e.getMessage(), e);
            throw new RuntimeException("Error deleting company-brand association", e);
        }
    }

    @Override
    public List<CompanyBrand> findByCompanyId(int companyId) {
        List<CompanyBrand> companyBrands = new ArrayList<>();
        String sql = "SELECT id, id_company, id_brand, created_at FROM company_brands WHERE id_company = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                companyBrands.add(mapResultSetToCompanyBrand(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding company-brand associations by company ID {}: {}", companyId, e.getMessage(), e);
            throw new RuntimeException("Database error finding company-brand associations", e);
        }
        return companyBrands;
    }

    @Override
    public List<CompanyBrand> findByBrandId(int brandId) {
        List<CompanyBrand> companyBrands = new ArrayList<>();
        String sql = "SELECT id, id_company, id_brand, created_at FROM company_brands WHERE id_brand = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, brandId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                companyBrands.add(mapResultSetToCompanyBrand(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding company-brand associations by brand ID {}: {}", brandId, e.getMessage(), e);
            throw new RuntimeException("Database error finding company-brand associations", e);
        }
        return companyBrands;
    }

    @Override
    public Optional<CompanyBrand> findByCompanyAndBrand(int companyId, int brandId) {
        String sql = "SELECT id, id_company, id_brand, created_at FROM company_brands WHERE id_company = ? AND id_brand = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, brandId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCompanyBrand(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding company-brand association (companyId: {}, brandId: {}): {}", companyId, brandId, e.getMessage(), e);
            throw new RuntimeException("Database error finding company-brand association", e);
        }
        return Optional.empty();
    }

    private CompanyBrand mapResultSetToCompanyBrand(ResultSet rs) throws SQLException {
        CompanyBrand cb = new CompanyBrand();
        cb.setId(rs.getInt("id"));
        cb.setIdCompany(rs.getInt("id_company"));
        cb.setIdBrand(rs.getInt("id_brand"));
        cb.setCreatedAt(rs.getTimestamp("created_at"));
        return cb;
    }
}