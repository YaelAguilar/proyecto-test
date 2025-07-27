package org.example.models;

import java.sql.Timestamp;

public class User {
    private int id;
    private int idRole;
    private String fullName;
    private String email;
    private String password;
    private Timestamp createdAt;

    public User() {}

    public User(int id, int idRole, String fullName, String email, String password, Timestamp createdAt) {
        this.id = id;
        this.idRole = idRole;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdRole() { return idRole; }
    public void setIdRole(int idRole) { this.idRole = idRole; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}