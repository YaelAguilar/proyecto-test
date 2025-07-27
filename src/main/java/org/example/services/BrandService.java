package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import org.example.models.Brand;
import org.example.models.dtos.BrandDto;
import org.example.repositories.IBrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BrandService {
    private static final Logger log = LoggerFactory.getLogger(BrandService.class);
    private final IBrandRepository brandRepository;

    public BrandService(IBrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        log.info("Obteniendo todas las marcas.");
        return brandRepository.findAll();
    }

    public Brand getBrandById(int id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Marca no encontrada con ID: " + id));
    }

    // MODIFICADO: Solo administradores pueden crear marcas
    public Brand createBrand(BrandDto brandDto) {
        if (brandDto.getNameBrand() == null || brandDto.getNameBrand().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre de la marca es obligatorio.");
        }
        if (brandRepository.findByName(brandDto.getNameBrand()).isPresent()) {
            throw new BadRequestResponse("Ya existe una marca con el nombre: " + brandDto.getNameBrand());
        }
        Brand newBrand = new Brand();
        newBrand.setNameBrand(brandDto.getNameBrand());
        log.info("Creando nueva marca: {}", brandDto.getNameBrand());
        return brandRepository.save(newBrand);
    }

    // MODIFICADO: Solo administradores pueden actualizar marcas
    public Brand updateBrand(int id, BrandDto brandDto) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Marca no encontrada con ID: " + id));

        if (brandDto.getNameBrand() == null || brandDto.getNameBrand().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre de la marca es obligatorio.");
        }
        Optional<Brand> brandWithName = brandRepository.findByName(brandDto.getNameBrand());
        if (brandWithName.isPresent() && brandWithName.get().getId() != id) {
            throw new BadRequestResponse("Ya existe otra marca con el nombre: " + brandDto.getNameBrand());
        }

        existingBrand.setNameBrand(brandDto.getNameBrand());
        log.info("Actualizando marca ID {}: {}", id, brandDto.getNameBrand());
        return brandRepository.update(existingBrand);
    }

    // MODIFICADO: Solo administradores pueden eliminar marcas
    public void deleteBrand(int id) {
        if (brandRepository.findById(id).isEmpty()) {
            throw new NotFoundResponse("Marca no encontrada con ID: " + id);
        }
        log.info("Eliminando marca con ID: {}", id);
        brandRepository.delete(id);
    }
}