package org.example.services;

import io.javalin.http.ForbiddenResponse; // Importar ForbiddenResponse
import io.javalin.http.NotFoundResponse;
import org.example.models.StateEquipment;
import org.example.models.dtos.StateEquipmentDto;
import org.example.repositories.IStateEquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class StateEquipmentService {
    private static final Logger log = LoggerFactory.getLogger(StateEquipmentService.class);
    private final IStateEquipmentRepository stateEquipmentRepository;

    public StateEquipmentService(IStateEquipmentRepository stateEquipmentRepository) {
        this.stateEquipmentRepository = stateEquipmentRepository;
    }

    public List<StateEquipment> getAllStates() {
        log.info("Obteniendo todos los estados de equipo.");
        return stateEquipmentRepository.findAll();
    }

    public StateEquipment getStateById(int id) {
        return stateEquipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Estado de equipo no encontrado con ID: " + id));
    }

    public StateEquipment createState(StateEquipmentDto stateDto) {
        // CORREGIDO: Prohibir la creación de nuevos estados
        throw new ForbiddenResponse("No se permite crear nuevos estados de equipo directamente por API.");
        /*
        // Código original (comentado porque ahora se prohíbe)
        if (stateDto.getState() == null || stateDto.getState().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre del estado es obligatorio.");
        }
        if (stateEquipmentRepository.findByState(stateDto.getState()).isPresent()) {
            throw new BadRequestResponse("Ya existe un estado de equipo con el nombre: " + stateDto.getState());
        }
        StateEquipment newState = new StateEquipment();
        newState.setState(stateDto.getState());
        log.info("Creando nuevo estado de equipo: {}", stateDto.getState());
        return stateEquipmentRepository.save(newState);
        */
    }

    public StateEquipment updateState(int id, StateEquipmentDto stateDto) {
        // CORREGIDO: Prohibir la actualización de estados
        throw new ForbiddenResponse("No se permite actualizar estados de equipo directamente por API.");
        /*
        // Código original (comentado porque ahora se prohíbe)
        StateEquipment existingState = stateEquipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Estado de equipo no encontrado con ID: " + id));

        if (stateDto.getState() == null || stateDto.getState().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre del estado es obligatorio.");
        }
        Optional<StateEquipment> stateWithName = stateEquipmentRepository.findByState(stateDto.getState());
        if (stateWithName.isPresent() && stateWithName.get().getId() != id) {
            throw new BadRequestResponse("Ya existe otro estado de equipo con el nombre: " + stateDto.getState());
        }

        existingState.setState(stateDto.getState());
        log.info("Actualizando estado de equipo ID {}: {}", id, stateDto.getState());
        return stateEquipmentRepository.update(existingState);
        */
    }

    public void deleteState(int id) {
        // CORREGIDO: Prohibir la eliminación de estados
        throw new ForbiddenResponse("No se permite eliminar estados de equipo directamente por API.");
        /*
        // Código original (comentado porque ahora se prohíbe)
        if (stateEquipmentRepository.findById(id).isEmpty()) {
            throw new NotFoundResponse("Estado de equipo no encontrado con ID: " + id);
        }
        log.info("Eliminando estado de equipo con ID: {}", id);
        stateEquipmentRepository.delete(id);
        */
    }
}