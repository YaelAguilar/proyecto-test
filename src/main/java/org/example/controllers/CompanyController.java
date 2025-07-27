package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.Brand;
import org.example.models.Company;
import org.example.services.CompanyService;
import org.example.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CompanyController {
    private static final Logger log = LoggerFactory.getLogger(CompanyController.class);
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void getAllCompanies(Context ctx) {
        log.info("Solicitud para obtener todas las compañías.");
        List<Company> companies = companyService.getAllCompanies();
        ctx.json(companies);
    }

    public void getCompanyById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        log.info("Solicitud para obtener compañía con ID: {}", id);
        Company company = companyService.getCompanyById(id);
        ctx.json(company);
    }

    public void createCompany(Context ctx) {
        Company company = ctx.bodyAsClass(Company.class); // Usa el modelo directo para crear, o un CompanyCreateDto
        log.info("Solicitud para crear nueva compañía: {}", company.getName());
        Company newCompany = companyService.createCompany(company);
        ctx.status(201).json(newCompany);
    }

    public void updateCompany(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Company updatedCompany = ctx.bodyAsClass(Company.class);
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx); // Obtener ID del usuario autenticado
        log.info("Solicitud para actualizar compañía ID {}: {}", id, updatedCompany.getName());
        Company result = companyService.updateCompany(id, updatedCompany, authUserId);
        ctx.json(result);
    }

    public void deleteCompany(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para eliminar compañía con ID: {}", id);
        companyService.deleteCompany(id, authUserId);
        ctx.status(204); // No Content
    }

    // Métodos para gestionar Company-Brand
    public void getBrandsByCompany(Context ctx) {
        int companyId = ctx.pathParamAsClass("companyId", Integer.class).get();
        log.info("Solicitud para obtener marcas de la compañía con ID: {}", companyId);
        List<Brand> brands = companyService.getBrandsByCompany(companyId);
        ctx.json(brands);
    }

    public void addBrandToCompany(Context ctx) {
        int companyId = ctx.pathParamAsClass("companyId", Integer.class).get();
        int brandId = ctx.pathParamAsClass("brandId", Integer.class).get();
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para asociar marca {} a compañía {}", brandId, companyId);
        companyService.addBrandToCompany(companyId, brandId, authUserId);
        ctx.status(200).result("Marca asociada a la compañía exitosamente.");
    }

    public void removeBrandFromCompany(Context ctx) {
        int companyId = ctx.pathParamAsClass("companyId", Integer.class).get();
        int brandId = ctx.pathParamAsClass("brandId", Integer.class).get();
        int authUserId = AuthUtils.getAuthenticatedUserId(ctx);
        log.info("Solicitud para desasociar marca {} de compañía {}", brandId, companyId);
        companyService.removeBrandFromCompany(companyId, brandId, authUserId);
        ctx.status(204); // No Content
    }
}