package org.example.models;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int idEquipment;
    private int idUser;
    private int idStar;
    private String body; // Contenido de la reseña
    private Timestamp createdAt;

    public Review() {}

    public Review(int id, int idEquipment, int idUser, int idStar, String body, Timestamp createdAt) {
        this.id = id;
        this.idEquipment = idEquipment;
        this.idUser = idUser;
        this.idStar = idStar;
        this.body = body;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEquipment() { return idEquipment; }
    public void setIdEquipment(int idEquipment) { this.idEquipment = idEquipment; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public int getIdStar() { return idStar; }
    public void setIdStar(int idStar) { this.idStar = idStar; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}