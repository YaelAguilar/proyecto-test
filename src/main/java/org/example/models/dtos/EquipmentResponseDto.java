package org.example.models.dtos;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class EquipmentResponseDto {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private String urlImage;
    private Timestamp createdAt;
    private String providerName;
    private String companyName;
    private String typeName;
    private String stateName;
    private String brandName;
    
    // NUEVO: IDs para facilitar filtros en el frontend
    private int idTypeEquipment;
    private int idStateEquipment;
    private int idBrand;
    private int idCompany;

    public EquipmentResponseDto() {}

    public EquipmentResponseDto(int id, String name, String description, BigDecimal price, String urlImage, Timestamp createdAt,
                                String providerName, String companyName, String typeName, String stateName, String brandName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.urlImage = urlImage;
        this.createdAt = createdAt;
        this.providerName = providerName;
        this.companyName = companyName;
        this.typeName = typeName;
        this.stateName = stateName;
        this.brandName = brandName;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    
    // NUEVO: Getters y Setters para IDs
    public int getIdTypeEquipment() { return idTypeEquipment; }
    public void setIdTypeEquipment(int idTypeEquipment) { this.idTypeEquipment = idTypeEquipment; }
    public int getIdStateEquipment() { return idStateEquipment; }
    public void setIdStateEquipment(int idStateEquipment) { this.idStateEquipment = idStateEquipment; }
    public int getIdBrand() { return idBrand; }
    public void setIdBrand(int idBrand) { this.idBrand = idBrand; }
    public int getIdCompany() { return idCompany; }
    public void setIdCompany(int idCompany) { this.idCompany = idCompany; }
}