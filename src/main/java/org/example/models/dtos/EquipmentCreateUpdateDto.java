package org.example.models.dtos;

import java.math.BigDecimal;

public class EquipmentCreateUpdateDto {
    private String name;
    private String description;
    private BigDecimal price;
    private String urlImage;
    private int typeId; // ID del tipo de equipo
    private int stateId; // ID del estado del equipo
    private int brandId; // ID de la marca

    public EquipmentCreateUpdateDto() {}

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public int getStateId() { return stateId; }
    public void setStateId(int stateId) { this.stateId = stateId; }
    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }
}