package org.example.models;

public class Client {
    private int id;
    private int idUser;

    public Client() {}

    public Client(int id, int idUser) {
        this.id = id;
        this.idUser = idUser;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
}