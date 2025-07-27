package org.example.models;

public class TypeEquipment {
    private int id;
    private String type;

    public TypeEquipment() {}

    public TypeEquipment(int id, String type) {
        this.id = id;
        this.type = type;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}