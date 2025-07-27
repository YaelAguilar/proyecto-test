package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.Equipment;
import org.example.models.dtos.EquipmentCreateUpdateDto;
import org.example.models.dtos.EquipmentResponseDto;
import org.example.services.EquipmentService;
import org.example.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EquipmentController {
    private static final Logger log = LoggerFactory.getLogger(EquipmentController.class);
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    public void getAllEquipment(Context ctx) {
        log.info("Solicitud para obtener todos los equipos.");
        List<EquipmentResponseDto> equipmentList = equipmentService.getAllEquipment();
        ctx.json(equipmentList);
    }

    // NUEVO: Método para obtener equipos por ID de proveedor
    public void getEquipmentByProviderId(Context ctx) {
        int providerId = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener equipos del proveedor con ID: {}", providerId);
        List<EquipmentResponseDto> equipmentList = equipmentService.getEquipmentByProviderId(providerId);
        ctx.json(equipmentList);
    }

    public void getEquipmentById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener equipo con ID: {}", id);
        EquipmentResponseDto equipment = equipmentService.getEquipmentById(id);
        ctx.json(equipment);
    }

    public void createEquipment(Context ctx) {
        EquipmentCreateUpdateDto createDto = ctx.bodyAsClass(EquipmentCreateUpdateDto.class);
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para crear equipo para usuario ID {}: {}", authUserId, createDto.getName());
        Equipment newEquipment = equipmentService.createEquipment(createDto, authUserId);
        ctx.status(201).json(newEquipment);
    }

    public void updateEquipment(Context ctx) {
        int equipmentId = ctx.pathParamAsClass("id", Integer.class).get();
        EquipmentCreateUpdateDto updateDto = ctx.bodyAsClass(EquipmentCreateUpdateDto.class);
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para actualizar equipo ID {}: {}", equipmentId, updateDto.getName());
        Equipment updatedEquipment = equipmentService.updateEquipment(equipmentId, updateDto, authUserId);
        ctx.json(updatedEquipment);
    }

    public void deleteEquipment(Context ctx) {
        int equipmentId = ctx.pathParamAsClass("id", Integer.class).get();
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para eliminar equipo ID: {} por usuario ID: {}", equipmentId, authUserId);
        equipmentService.deleteEquipment(equipmentId, authUserId);
        ctx.status(204); // No Content
    }
}