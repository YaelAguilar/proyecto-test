package org.example.models;

public class Provider {
    private int id;
    private int idUser;
    private int idCompany; // Puede ser 0 o un valor especial si no hay compañía asignada

    public Provider() {}

    public Provider(int id, int idUser, int idCompany) {
        this.id = id;
        this.idUser = idUser;
        this.idCompany = idCompany;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public int getIdCompany() { return idCompany; }
    public void setIdCompany(int idCompany) { this.idCompany = idCompany; }
}