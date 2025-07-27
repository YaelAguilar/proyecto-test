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
            if (!role.equalsIgnoreCase("cliente") && !role.equalsIgnoreCase("proveedor")) {
                throw new io.javalin.http.ForbiddenResponse("Acceso denegado: solo clientes y proveedores pueden acceder a perfiles de usuario.");
            }
            AuthUtils.requireOwnership(ctx, Integer.parseInt(ctx.pathParam("id")));
            userController.getUserById(ctx);
        });

        // OBTENER EL PROVEEDOR ASOCIADO A UN USUARIO
        app.get("/api/users/{id}/provider", ctx -> {
            AuthUtils.requireRole(ctx, "proveedor"); // Solo proveedores pueden acceder a esta info
            AuthUtils.requireOwnership(ctx, Integer.parseInt(ctx.pathParam("id"))); // Y solo su propia info de proveedor
            userController.getProviderByUserId(ctx); // Llama al nuevo método del controlador
        });


        // --- Rutas de Compañías ---
        app.get("/api/companies", companyController::getAllCompanies); // Obtener todas las compañías (público)

        app.post("/api/companies", ctx -> { // Crear nueva compañía
            AuthUtils.requireRole(ctx, "proveedor");
            companyController.createCompany(ctx);
        });

        app.get("/api/companies/{id}", companyController::getCompanyById); // Obtener compañía por ID (público)

        app.put("/api/companies/{id}", ctx -> { // Actualizar compañía
            AuthUtils.requireRole(ctx, "proveedor");
            companyController.updateCompany(ctx);
        });

        app.delete("/api/companies/{id}", ctx -> { // Eliminar compañía
            AuthUtils.requireRole(ctx, "proveedor");
            companyController.deleteCompany(ctx);
        });

        // --- Rutas para Company-Brand (anidadas bajo una compañía específica) ---
        app.get("/api/companies/{id}/brands", companyController::getBrandsByCompany); // Obtener marcas de una compañía (público)

        app.post("/api/companies/{id}/brands/{brandId}", ctx -> { // Añadir marca a compañía
            AuthUtils.requireRole(ctx, "proveedor");
            companyController.addBrandToCompany(ctx);
        });

        app.delete("/api/companies/{id}/brands/{brandId}", ctx -> { // Eliminar marca de compañía
            AuthUtils.requireRole(ctx, "proveedor");
            companyController.removeBrandFromCompany(ctx);
        });

        // --- Rutas de Marcas (Se mantienen CRUD) ---
        app.get("/api/brands", brandController::getAllBrands); // Obtener todas las marcas (público)

        app.post("/api/brands", ctx -> { // Crear nueva marca
            AuthUtils.requireRole(ctx, "proveedor");
            brandController.createBrand(ctx);
        });

        app.get("/api/brands/{id}", brandController::getBrandById); // Obtener marca por ID (público)

        app.put("/api/brands/{id}", ctx -> { // Actualizar marca
            AuthUtils.requireRole(ctx, "proveedor");
            brandController.updateBrand(ctx);
        });

        app.delete("/api/brands/{id}", ctx -> { // Eliminar marca
            AuthUtils.requireRole(ctx, "proveedor");
            brandController.deleteBrand(ctx);
        });

        // --- Rutas de Tipos de Equipo (Solo GET, ya que los valores son fijos) ---
        app.get("/api/types-equipment", typeEquipmentController::getAllTypes); // Obtener todos los tipos (público)
        app.get("/api/types-equipment/{id}", typeEquipmentController::getTypeById); // Obtener tipo por ID (público)

        // --- Rutas de Estados de Equipo (Solo GET, ya que los valores son fijos) ---
        app.get("/api/states-equipment", stateEquipmentController::getAllStates); // Obtener todos los estados (público)
        app.get("/api/states-equipment/{id}", stateEquipmentController::getStateById); // Obtener estado por ID (público)

        // --- Rutas de Equipos ---
        app.get("/api/equipment", equipmentController::getAllEquipment); // Obtener todos los equipos (público)
        // NUEVO: Obtener equipos por proveedor
        app.get("/api/equipment/provider/{id}", equipmentController::getEquipmentByProviderId);


        app.post("/api/equipment", ctx -> { // Crear nuevo equipo
            AuthUtils.requireRole(ctx, "proveedor");
            equipmentController.createEquipment(ctx);
        });

        app.get("/api/equipment/{id}", equipmentController::getEquipmentById); // Obtener equipo por ID (público)

        app.put("/api/equipment/{id}", ctx -> { // Actualizar equipo
            AuthUtils.requireRole(ctx, "proveedor");
            equipmentController.updateEquipment(ctx);
        });

        app.delete("/api/equipment/{id}", ctx -> { // Eliminar equipo
            AuthUtils.requireRole(ctx, "proveedor");
            equipmentController.deleteEquipment(ctx);
        });

        // --- Rutas de Reseñas ---
        app.post("/api/reviews", ctx -> { // Crear nueva reseña
            AuthUtils.requireRole(ctx, "cliente");
            reviewController.createReview(ctx);
        });

        app.delete("/api/reviews/{id}", ctx -> { // Eliminar reseña
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
        app.post("/api/favorites", ctx -> { // Añadir equipo a favoritos
            AuthUtils.requireRole(ctx, "cliente");
            favoriteController.addFavorite(ctx);
        });

        app.delete("/api/favorites/{id}", ctx -> { // Eliminar favorito
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