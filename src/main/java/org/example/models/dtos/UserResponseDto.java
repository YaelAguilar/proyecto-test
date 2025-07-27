package org.example.models.dtos;

import java.sql.Timestamp;

public class UserResponseDto {
    private int id;
    private String fullName;
    private String email;
    private String roleName; // Nombre del rol
    private Timestamp createdAt;

    public UserResponseDto() {}

    public UserResponseDto(int id, String fullName, String email, String roleName, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRoleName() { return roleName; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters (si se necesita mapear desde un objeto User antes de enviar)
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}