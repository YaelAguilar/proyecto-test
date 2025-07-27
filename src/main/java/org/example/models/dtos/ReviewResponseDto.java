package org.example.models.dtos;

import java.sql.Timestamp;

public class ReviewResponseDto {
    private int id;
    private int equipmentId;
    private String equipmentName; // Nombre del equipo
    private int userId;
    private String userName; // Nombre del usuario que hizo la reseña
    private int starValue; // Valor numérico de la estrella
    private String body;
    private Timestamp createdAt;

    public ReviewResponseDto() {}

    public ReviewResponseDto(int id, int equipmentId, String equipmentName, int userId, String userName, int starValue, String body, Timestamp createdAt) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.userId = userId;
        this.userName = userName;
        this.starValue = starValue;
        this.body = body;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getStarValue() { return starValue; }
    public void setStarValue(int starValue) { this.starValue = starValue; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}