package org.example.models.dtos;

// DTO para crear o actualizar un estado de equipo
public class StateEquipmentDto {
    private String state;

    public StateEquipmentDto() {}

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}