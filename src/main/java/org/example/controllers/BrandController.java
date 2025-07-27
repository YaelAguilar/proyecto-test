package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.Brand;
import org.example.models.dtos.BrandDto;
import org.example.services.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BrandController {
    private static final Logger log = LoggerFactory.getLogger(BrandController.class);
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    public void getAllBrands(Context ctx) {
        log.info("Solicitud para obtener todas las marcas.");
        List<Brand> brands = brandService.getAllBrands();
        ctx.json(brands);
    }

    public void getBrandById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener marca con ID: {}", id);
        Brand brand = brandService.getBrandById(id);
        ctx.json(brand);
    }

    public void createBrand(Context ctx) {
        BrandDto brandDto = ctx.bodyAsClass(BrandDto.class);
        log.info("Solicitud para crear nueva marca: {}", brandDto.getNameBrand());
        Brand newBrand = brandService.createBrand(brandDto);
        ctx.status(201).json(newBrand);
    }

    public void updateBrand(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        BrandDto brandDto = ctx.bodyAsClass(BrandDto.class);
        log.info("Solicitud para actualizar marca ID {}: {}", id, brandDto.getNameBrand());
        Brand updatedBrand = brandService.updateBrand(id, brandDto);
        ctx.json(updatedBrand);
    }

    public void deleteBrand(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para eliminar marca con ID: {}", id);
        brandService.deleteBrand(id);
        ctx.status(204); // 204 No Content
    }
}