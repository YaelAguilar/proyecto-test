package org.example.models;

import java.sql.Timestamp;

public class Role {
    private int id;
    private String role;
    private Timestamp createdAt;

    public Role() {}

    public Role(int id, String role, Timestamp createdAt) {
        this.id = id;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}