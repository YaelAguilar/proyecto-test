package org.example.models.dtos;

public class ReviewCreateUpdateDto {
    private int equipmentId;
    private int starValue; // El valor de las estrellas (1-5)
    private String body; // El texto de la reseña

    public ReviewCreateUpdateDto() {}

    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }
    public int getStarValue() { return starValue; }
    public void setStarValue(int starValue) { this.starValue = starValue; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}