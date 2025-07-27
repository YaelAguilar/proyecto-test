package org.example.repositories;

import com.zaxxer.hikari.HikariDataSource;
import org.example.models.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; // <-- CORREGIDO: Importación de ArrayList
import java.util.List;
import java.util.Optional;

public class CompanyRepositoryImpl implements ICompanyRepository {
    private static final Logger log = LoggerFactory.getLogger(CompanyRepositoryImpl.class);
    private final HikariDataSource dataSource;

    public CompanyRepositoryImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Company> findById(int id) {
        String sql = "SELECT id, name, email, phone, address, web_site, created_at FROM company WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding company by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding company", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Company> findByName(String name) {
        String sql = "SELECT id, name, email, phone, address, web_site, created_at FROM company WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding company by name {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Database error finding company by name", e);
        }
        return Optional.empty();
    }

    @Override // <-- CORREGIDO: Implementación de findAll
    public List<Company> findAll() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT id, name, email, phone, address, web_site, created_at FROM company";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding all companies", e);
            throw new RuntimeException("Database error finding all companies", e);
        }
        return companies;
    }

    @Override
    public Company save(Company company) {
        String sql = "INSERT INTO company (name, email, phone, address, web_site) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, company.getName());
            stmt.setString(2, company.getEmail());
            stmt.setString(3, company.getPhone());
            stmt.setString(4, company.getAddress());
            stmt.setString(5, company.getWebSite());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating company failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    company.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating company failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            log.error("Error saving company {}: {}", company.getName(), e.getMessage(), e);
            throw new RuntimeException("Error saving company", e);
        }
        return company;
    }

    @Override
    public Company update(Company company) {
        String sql = "UPDATE company SET name = ?, email = ?, phone = ?, address = ?, web_site = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, company.getName());
            stmt.setString(2, company.getEmail());
            stmt.setString(3, company.getPhone());
            stmt.setString(4, company.getAddress());
            stmt.setString(5, company.getWebSite());
            stmt.setInt(6, company.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating company {}: {}", company.getId(), e.getMessage(), e);
            throw new RuntimeException("Error updating company", e);
        }
        return company;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM company WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting company by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting company", e);
        }
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setId(rs.getInt("id"));
        company.setName(rs.getString("name"));
        company.setEmail(rs.getString("email"));
        company.setPhone(rs.getString("phone"));
        company.setAddress(rs.getString("address"));
        company.setWebSite(rs.getString("web_site"));
        company.setCreatedAt(rs.getTimestamp("created_at"));
        return company;
    }
}