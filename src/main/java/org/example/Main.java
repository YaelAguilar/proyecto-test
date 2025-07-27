package org.example;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.HttpResponseException;
import org.example.configs.DatabaseConfig;
import org.example.controllers.*;
import org.example.repositories.*;
import org.example.routes.ApiRoutes;
import org.example.services.*;
import org.example.utils.JwtManager;
import org.example.models.dtos.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // --- 1. Cargar Variables de Entorno ---
        Dotenv dotenv = Dotenv.load();
        int port = Integer.parseInt(dotenv.get("APP_PORT", "7000"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET", "super_secret_default_key_change_me"));

        // --- 2. Inicializar la Conexión a la Base de Datos ---
        HikariDataSource dataSource = DatabaseConfig.getDataSource(dotenv);

        // --- 3. Inicializar Repositorios (Capa de Acceso a Datos) ---
        IRoleRepository roleRepository = new RoleRepositoryImpl(dataSource);
        IUserRepository userRepository = new UserRepositoryImpl(dataSource);
        IClientRepository clientRepository = new ClientRepositoryImpl(dataSource);
        ICompanyRepository companyRepository = new CompanyRepositoryImpl(dataSource);
        IProviderRepository providerRepository = new ProviderRepositoryImpl(dataSource);
        IBrandRepository brandRepository = new BrandRepositoryImpl(dataSource);
        ICompanyBrandRepository companyBrandRepository = new CompanyBrandRepositoryImpl(dataSource);
        ITypeEquipmentRepository typeEquipmentRepository = new TypeEquipmentRepositoryImpl(dataSource);
        IStateEquipmentRepository stateEquipmentRepository = new StateEquipmentRepositoryImpl(dataSource);
        IEquipmentRepository equipmentRepository = new EquipmentRepositoryImpl(dataSource);
        IStarRepository starRepository = new StarRepositoryImpl(dataSource);
        IReviewRepository reviewRepository = new ReviewRepositoryImpl(dataSource);
        IFavoriteRepository favoriteRepository = new FavoriteRepositoryImpl(dataSource);

        // --- 4. Inicializar Utilidades para Autenticación/Autorización ---
        JwtManager jwtManager = new JwtManager(System.getProperty("JWT_SECRET"));

        // --- 5. Inicializar Servicios (Capa de Lógica de Negocio) ---
        AuthService authService = new AuthService(userRepository, roleRepository, clientRepository, providerRepository, companyRepository, jwtManager);
        UserService userService = new UserService(userRepository, roleRepository, providerRepository);
        CompanyService companyService = new CompanyService(companyRepository, brandRepository, companyBrandRepository);
        BrandService brandService = new BrandService(brandRepository);
        TypeEquipmentService typeEquipmentService = new TypeEquipmentService(typeEquipmentRepository);
        StateEquipmentService stateEquipmentService = new StateEquipmentService(stateEquipmentRepository);
        // MODIFICADO: Añadido companyBrandRepository al constructor de EquipmentService
        EquipmentService equipmentService = new EquipmentService(equipmentRepository, providerRepository, typeEquipmentRepository, stateEquipmentRepository, brandRepository, userRepository, companyRepository, companyBrandRepository);
        ReviewService reviewService = new ReviewService(reviewRepository, equipmentRepository, userRepository, starRepository);
        FavoriteService favoriteService = new FavoriteService(favoriteRepository, equipmentRepository, userRepository);

        // --- 6. Inicializar Controladores (Capa de API/HTTP) ---
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService);
        CompanyController companyController = new CompanyController(companyService);
        BrandController brandController = new BrandController(brandService);
        TypeEquipmentController typeEquipmentController = new TypeEquipmentController(typeEquipmentService);
        StateEquipmentController stateEquipmentController = new StateEquipmentController(stateEquipmentService);
        EquipmentController equipmentController = new EquipmentController(equipmentService);
        ReviewController reviewController = new ReviewController(reviewService);
        FavoriteController favoriteController = new FavoriteController(favoriteService);

        // --- 7. Configurar y Lanzar la Aplicación Javalin ---
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.showJavalinBanner = false;
        }).start(port);

        // --- CORS global ---
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Authorization,Content-Type");
        });
        app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Authorization,Content-Type");
            ctx.status(200);
        });

        // --- 8. Definir Rutas de la API ---
        ApiRoutes.setupRoutes(app, authController, userController, companyController,
                equipmentController, reviewController, favoriteController,
                brandController, typeEquipmentController, stateEquipmentController);

        // --- 9. Manejo Global de Excepciones ---
        app.exception(HttpResponseException.class, (e, ctx) -> {
            log.warn("HTTP Response Exception (Status {}): {}", e.getStatus(), e.getMessage());
            ctx.status(e.getStatus());
            ctx.json(new ErrorResponse(e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            log.error("Ocurrió un error inesperado en la aplicación: {}", e.getMessage(), e);
            ctx.status(500).json(new InternalServerErrorResponse("Ocurrió un error interno en el servidor. Por favor, inténtelo de nuevo más tarde."));
        });

        // --- 10. Hook de Apagado para Limpieza de Recursos ---
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                log.info("HikariCP connection pool closed gracefully.");
            }
        }));

        log.info("Servidor Javalin iniciado exitosamente en el puerto {}", port);
    }
}