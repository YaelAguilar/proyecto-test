package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.StateEquipment;
import org.example.models.dtos.StateEquipmentDto;
import org.example.services.StateEquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StateEquipmentController {
    private static final Logger log = LoggerFactory.getLogger(StateEquipmentController.class);
    private final StateEquipmentService stateEquipmentService;

    public StateEquipmentController(StateEquipmentService stateEquipmentService) {
        this.stateEquipmentService = stateEquipmentService;
    }

    public void getAllStates(Context ctx) {
        log.info("Solicitud para obtener todos los estados de equipo.");
        List<StateEquipment> states = stateEquipmentService.getAllStates();
        ctx.json(states);
    }

    public void getStateById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener estado de equipo con ID: {}", id);
        StateEquipment state = stateEquipmentService.getStateById(id);
        ctx.json(state);
    }

    public void createState(Context ctx) {
        StateEquipmentDto stateDto = ctx.bodyAsClass(StateEquipmentDto.class);
        log.info("Solicitud para crear nuevo estado de equipo: {}", stateDto.getState());
        StateEquipment newState = stateEquipmentService.createState(stateDto);
        ctx.status(201).json(newState);
    }

    public void updateState(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        StateEquipmentDto stateDto = ctx.bodyAsClass(StateEquipmentDto.class);
        log.info("Solicitud para actualizar estado de equipo ID {}: {}", id, stateDto.getState());
        StateEquipment updatedState = stateEquipmentService.updateState(id, stateDto);
        ctx.json(updatedState);
    }

    public void deleteState(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para eliminar estado de equipo con ID: {}", id);
        stateEquipmentService.deleteState(id);
        ctx.status(204); // 204 No Content
    }
}