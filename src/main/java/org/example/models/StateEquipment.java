package org.example.models;

public class StateEquipment {
    private int id;
    private String state;

    public StateEquipment() {}

    public StateEquipment(int id, String state) {
        this.id = id;
        this.state = state;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}