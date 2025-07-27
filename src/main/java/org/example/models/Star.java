package org.example.models;

public class Star {
    private int id;
    private int value; // Valor numérico de la estrella (ej. 1, 2, 3, 4, 5)

    public Star() {}

    public Star(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
}