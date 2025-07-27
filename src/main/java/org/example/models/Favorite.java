package org.example.models;

import java.sql.Timestamp;

public class Favorite {
    private int id;
    private int idUser;
    private int idEquipment;
    private Timestamp createdAt;

    public Favorite() {}

    public Favorite(int id, int idUser, int idEquipment, Timestamp createdAt) {
        this.id = id;
        this.idUser = idUser;
        this.idEquipment = idEquipment;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public int getIdEquipment() { return idEquipment; }
    public void setIdEquipment(int idEquipment) { this.idEquipment = idEquipment; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}