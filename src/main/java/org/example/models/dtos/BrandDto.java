package org.example.models.dtos;

// DTO para crear o actualizar una marca
public class BrandDto {
    private String nameBrand;

    public BrandDto() {}

    public String getNameBrand() { return nameBrand; }
    public void setNameBrand(String nameBrand) { this.nameBrand = nameBrand; }
}