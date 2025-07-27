package org.example.routes;

import io.javalin.Javalin;
import org.example.controllers.*;
import org.example.utils.AuthUtils;

public class ApiRoutes {

    public static void setupRoutes(Javalin app,
                                   AuthController authController,
                                   UserController userController,
                                   CompanyController companyController,
                                   EquipmentController equipmentController,
                                   ReviewController reviewController,
                                   FavoriteController favoriteController,
                                   BrandController brandController,
                                   TypeEquipmentController typeEquipmentController,
                                   StateEquipmentController stateEquipmentController) {

        // --- Rutas de Autenticación (públicas) ---
        app.post("/api/register", authController::register);
        app.post("/api/login", authController::login);

        // --- Rutas de Usuarios ---
        app.get("/api/users/{id}", ctx -> {
            String role = AuthUtils.getAuthenticatedUserRole(ctx);
            if (!role.equalsIgnoreCase("cliente") && !role.equalsIgnoreCase("proveedor") && !role.equalsIgnoreCase("administrador")) {
                throw new io.javalin.http.ForbiddenResponse("Acceso denegado.");
            }
            AuthUtils.requireOwnership(ctx, Integer.parseInt(ctx.pathParam("id")));
            userController.getUserById(ctx);
        });

        // OBTENER EL PROVEEDOR ASOCIADO A UN USUARIO
        app.get("/api/users/{id}/provider", ctx -> {
            AuthUtils.requireRole(ctx, "proveedor");
            AuthUtils.requireOwnership(ctx, Integer.parseInt(ctx.pathParam("id")));
            userController.getProviderByUserId(ctx);
        });

        // --- Rutas de Compañías ---
        app.get("/api/companies", companyController::getAllCompanies); // Público

        // MODIFICADO: Solo administradores pueden crear/actualizar/eliminar empresas
        app.post("/api/companies", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            companyController.createCompany(ctx);
        });

        app.get("/api/companies/{id}", companyController::getCompanyById); // Público

        app.put("/api/companies/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            companyController.updateCompany(ctx);
        });

        app.delete("/api/companies/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            companyController.deleteCompany(ctx);
        });

        // --- Rutas para Company-Brand ---
        app.get("/api/companies/{companyId}/brands", companyController::getBrandsByCompany); // Público

        // MODIFICADO: Solo administradores pueden asociar/desasociar marcas
        app.post("/api/companies/{companyId}/brands/{brandId}", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            companyController.addBrandToCompany(ctx);
        });

        app.delete("/api/companies/{companyId}/brands/{brandId}", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            companyController.removeBrandFromCompany(ctx);
        });

        // --- Rutas de Marcas ---
        app.get("/api/brands", brandController::getAllBrands); // Público

        // MODIFICADO: Solo administradores pueden gestionar marcas
        app.post("/api/brands", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            brandController.createBrand(ctx);
        });

        app.get("/api/brands/{id}", brandController::getBrandById); // Público

        app.put("/api/brands/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            brandController.updateBrand(ctx);
        });

        app.delete("/api/brands/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "administrador");
            brandController.deleteBrand(ctx);
        });

        // --- Rutas de Tipos de Equipo (Solo lectura) ---
        app.get("/api/types-equipment", typeEquipmentController::getAllTypes); // Público
        app.get("/api/types-equipment/{id}", typeEquipmentController::getTypeById); // Público

        // --- Rutas de Estados de Equipo (Solo lectura) ---
        app.get("/api/states-equipment", stateEquipmentController::getAllStates); // Público
        app.get("/api/states-equipment/{id}", stateEquipmentController::getStateById); // Público

        // --- Rutas de Equipos ---
        app.get("/api/equipment", equipmentController::getAllEquipment); // Público
        
        // NUEVO: Endpoint de búsqueda con filtros
        app.get("/api/equipment/search", equipmentController::searchEquipment); // Público
        
        app.get("/api/equipment/provider/{id}", equipmentController::getEquipmentByProviderId); // Público

        app.post("/api/equipment", ctx -> {
            AuthUtils.requireRole(ctx, "proveedor");
            equipmentController.createEquipment(ctx);
        });

        app.get("/api/equipment/{id}", equipmentController::getEquipmentById); // Público

        app.put("/api/equipment/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "proveedor");
            equipmentController.updateEquipment(ctx);
        });

        app.delete("/api/equipment/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "proveedor");
            equipmentController.deleteEquipment(ctx);
        });

        // --- Rutas de Reseñas ---
        app.post("/api/reviews", ctx -> {
            AuthUtils.requireRole(ctx, "cliente");
            reviewController.createReview(ctx);
        });

        app.delete("/api/reviews/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "cliente");
            reviewController.deleteReview(ctx);
        });

        // Reseñas de un equipo (públicas)
        app.get("/api/equipment/{id}/reviews", reviewController::getReviewsForEquipment);

        // Reseñas de un usuario (propias)
        app.get("/api/users/{id}/reviews", ctx -> {
            AuthUtils.requireRole(ctx, "cliente");
            AuthUtils.requireOwnership(ctx, Integer.parseInt(ctx.pathParam("id")));
            reviewController.getReviewsByUser(ctx);
        });

        // --- Rutas de Favoritos ---
        app.post("/api/favorites", ctx -> {
            AuthUtils.requireRole(ctx, "cliente");
            favoriteController.addFavorite(ctx);
        });

        app.delete("/api/favorites/{id}", ctx -> {
            AuthUtils.requireRole(ctx, "cliente");
            favoriteController.removeFavorite(ctx);
        });

        // Favoritos de un usuario (propios)
        app.get("/api/users/{id}/favorites", ctx -> {
            AuthUtils.requireRole(ctx, "cliente");
            AuthUtils.requireOwnership(ctx, Integer.parseInt(ctx.pathParam("id")));
            favoriteController.getFavoritesByUser(ctx);
        });
    }
}