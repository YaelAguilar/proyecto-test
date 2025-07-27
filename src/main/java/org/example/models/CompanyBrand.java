package org.example.models;

import java.sql.Timestamp;

// Tabla intermedia para relación muchos a muchos entre Company y Brands
public class CompanyBrand {
    private int id;
    private int idCompany;
    private int idBrand;
    private Timestamp createdAt;

    public CompanyBrand() {}

    public CompanyBrand(int id, int idCompany, int idBrand, Timestamp createdAt) {
        this.id = id;
        this.idCompany = idCompany;
        this.idBrand = idBrand;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdCompany() { return idCompany; }
    public void setIdCompany(int idCompany) { this.idCompany = idCompany; }
    public int getIdBrand() { return idBrand; }
    public void setIdBrand(int idBrand) { this.idBrand = idBrand; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}