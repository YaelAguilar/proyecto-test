package org.example.models.dtos;

// DTO para añadir un equipo a favoritos
public class FavoriteRequestDto {
    private int equipmentId;

    public FavoriteRequestDto() {}

    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }
}