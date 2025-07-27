package org.example.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Equipment {
    private int id;
    private int idProvider;
    private int idTypeEquipment;
    private String name;
    private String description;
    private BigDecimal price;
    private int idStateEquipment;
    private String urlImage;
    private int idBrand;
    private Timestamp createdAt;

    public Equipment() {}

    public Equipment(int id, int idProvider, int idTypeEquipment, String name, String description,
                     BigDecimal price, int idStateEquipment, String urlImage, int idBrand, Timestamp createdAt) {
        this.id = id;
        this.idProvider = idProvider;
        this.idTypeEquipment = idTypeEquipment;
        this.name = name;
        this.description = description;
        this.price = price;
        this.idStateEquipment = idStateEquipment;
        this.urlImage = urlImage;
        this.idBrand = idBrand;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdProvider() { return idProvider; }
    public void setIdProvider(int idProvider) { this.idProvider = idProvider; }
    public int getIdTypeEquipment() { return idTypeEquipment; }
    public void setIdTypeEquipment(int idTypeEquipment) { this.idTypeEquipment = idTypeEquipment; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getIdStateEquipment() { return idStateEquipment; }
    public void setIdStateEquipment(int idStateEquipment) { this.idStateEquipment = idStateEquipment; }
    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public int getIdBrand() { return idBrand; }
    public void setIdBrand(int idBrand) { this.idBrand = idBrand; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}