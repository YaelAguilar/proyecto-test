package org.example.models;

public class Brand {
    private int id;
    private String nameBrand;

    public Brand() {}

    public Brand(int id, String nameBrand) {
        this.id = id;
        this.nameBrand = nameBrand;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNameBrand() { return nameBrand; }
    public void setNameBrand(String nameBrand) { this.nameBrand = nameBrand; }
}