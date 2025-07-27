package org.example.services;

import io.javalin.http.ForbiddenResponse; // Importar ForbiddenResponse
import io.javalin.http.NotFoundResponse;
import org.example.models.TypeEquipment;
import org.example.models.dtos.TypeEquipmentDto;
import org.example.repositories.ITypeEquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class TypeEquipmentService {
    private static final Logger log = LoggerFactory.getLogger(TypeEquipmentService.class);
    private final ITypeEquipmentRepository typeEquipmentRepository;

    public TypeEquipmentService(ITypeEquipmentRepository typeEquipmentRepository) {
        this.typeEquipmentRepository = typeEquipmentRepository;
    }

    public List<TypeEquipment> getAllTypes() {
        log.info("Obteniendo todos los tipos de equipo.");
        return typeEquipmentRepository.findAll();
    }

    public TypeEquipment getTypeById(int id) {
        return typeEquipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Tipo de equipo no encontrado con ID: " + id));
    }

    public TypeEquipment createType(TypeEquipmentDto typeDto) {
        // CORREGIDO: Prohibir la creación de nuevos tipos
        throw new ForbiddenResponse("No se permite crear nuevos tipos de equipo directamente por API.");
        /*
        // Código original (comentado porque ahora se prohíbe)
        if (typeDto.getType() == null || typeDto.getType().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre del tipo de equipo es obligatorio.");
        }
        if (typeEquipmentRepository.findByType(typeDto.getType()).isPresent()) {
            throw new BadRequestResponse("Ya existe un tipo de equipo con el nombre: " + typeDto.getType());
        }
        TypeEquipment newType = new TypeEquipment();
        newType.setType(typeDto.getType());
        log.info("Creando nuevo tipo de equipo: {}", typeDto.getType());
        return typeEquipmentRepository.save(newType);
        */
    }

    public TypeEquipment updateType(int id, TypeEquipmentDto typeDto) {
        // CORREGIDO: Prohibir la actualización de tipos
        throw new ForbiddenResponse("No se permite actualizar tipos de equipo directamente por API.");
        /*
        // Código original (comentado porque ahora se prohíbe)
        TypeEquipment existingType = typeEquipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Tipo de equipo no encontrado con ID: " + id));

        if (typeDto.getType() == null || typeDto.getType().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre del tipo de equipo es obligatorio.");
        }
        Optional<TypeEquipment> typeWithName = typeEquipmentRepository.findByType(typeDto.getType());
        if (typeWithName.isPresent() && typeWithName.get().getId() != id) {
            throw new BadRequestResponse("Ya existe otro tipo de equipo con el nombre: " + typeDto.getType());
        }

        existingType.setType(typeDto.getType());
        log.info("Actualizando tipo de equipo ID {}: {}", id, typeDto.getType());
        return typeEquipmentRepository.update(existingType);
        */
    }

    public void deleteType(int id) {
        // CORREGIDO: Prohibir la eliminación de tipos
        throw new ForbiddenResponse("No se permite eliminar tipos de equipo directamente por API.");
        /*
        // Código original (comentado porque ahora se prohíbe)
        if (typeEquipmentRepository.findById(id).isEmpty()) {
            throw new NotFoundResponse("Tipo de equipo no encontrado con ID: " + id);
        }
        log.info("Eliminando tipo de equipo con ID: {}", id);
        typeEquipmentRepository.delete(id);
        */
    }
}