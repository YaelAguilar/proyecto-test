package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.TypeEquipment;
import org.example.models.dtos.TypeEquipmentDto;
import org.example.services.TypeEquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TypeEquipmentController {
    private static final Logger log = LoggerFactory.getLogger(TypeEquipmentController.class);
    private final TypeEquipmentService typeEquipmentService;

    public TypeEquipmentController(TypeEquipmentService typeEquipmentService) {
        this.typeEquipmentService = typeEquipmentService;
    }

    public void getAllTypes(Context ctx) {
        log.info("Solicitud para obtener todos los tipos de equipo.");
        List<TypeEquipment> types = typeEquipmentService.getAllTypes();
        ctx.json(types);
    }

    public void getTypeById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener tipo de equipo con ID: {}", id);
        TypeEquipment type = typeEquipmentService.getTypeById(id);
        ctx.json(type);
    }

    public void createType(Context ctx) {
        TypeEquipmentDto typeDto = ctx.bodyAsClass(TypeEquipmentDto.class);
        log.info("Solicitud para crear nuevo tipo de equipo: {}", typeDto.getType());
        TypeEquipment newType = typeEquipmentService.createType(typeDto);
        ctx.status(201).json(newType);
    }

    public void updateType(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        TypeEquipmentDto typeDto = ctx.bodyAsClass(TypeEquipmentDto.class);
        log.info("Solicitud para actualizar tipo de equipo ID {}: {}", id, typeDto.getType());
        TypeEquipment updatedType = typeEquipmentService.updateType(id, typeDto);
        ctx.json(updatedType);
    }

    public void deleteType(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para eliminar tipo de equipo con ID: {}", id);
        typeEquipmentService.deleteType(id);
        ctx.status(204); // 204 No Content
    }
}