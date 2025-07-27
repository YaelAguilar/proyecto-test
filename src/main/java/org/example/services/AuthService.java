package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;
import org.example.models.Company;
import org.example.models.Provider;
import org.example.models.Role;
import org.example.models.User;
import org.example.models.dtos.AuthResponseDto;
import org.example.models.dtos.UserLoginDto;
import org.example.models.dtos.UserRegisterDto;
import org.example.repositories.IClientRepository;
import org.example.repositories.ICompanyRepository;
import org.example.repositories.IProviderRepository;
import org.example.repositories.IRoleRepository;
import org.example.repositories.IUserRepository;
import org.example.utils.JwtManager;
import org.example.utils.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IClientRepository clientRepository;
    private final IProviderRepository providerRepository;
    private final ICompanyRepository companyRepository;
    private final JwtManager jwtManager;

    public AuthService(IUserRepository userRepository, IRoleRepository roleRepository,
                       IClientRepository clientRepository, IProviderRepository providerRepository,
                       ICompanyRepository companyRepository,
                       JwtManager jwtManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.providerRepository = providerRepository;
        this.companyRepository = companyRepository;
        this.jwtManager = jwtManager;
    }

    public AuthResponseDto registerUser(UserRegisterDto registerDto) {
        // --- 1. Validaciones del DTO ---
        if (registerDto.getEmail() == null || registerDto.getEmail().isEmpty() ||
                registerDto.getPassword() == null || registerDto.getPassword().isEmpty() ||
                registerDto.getFullName() == null || registerDto.getFullName().isEmpty() ||
                registerDto.getUserType() == null || registerDto.getUserType().isEmpty()) {
            throw new BadRequestResponse("Todos los campos obligatorios (email, password, fullName, userType) deben ser proporcionados.");
        }

        if (!registerDto.getUserType().equalsIgnoreCase("cliente") && !registerDto.getUserType().equalsIgnoreCase("proveedor")) {
            throw new BadRequestResponse("Tipo de usuario no válido. Debe ser 'cliente' o 'proveedor'.");
        }

        // --- 2. Verificar existencia del email ---
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new BadRequestResponse("El email ya está registrado.");
        }

        // --- 3. Obtener el rol y hashear contraseña ---
        Optional<Role> roleOpt = roleRepository.findByRoleName(registerDto.getUserType().toLowerCase());
        if (roleOpt.isEmpty()) {
            log.error("Rol '{}' no encontrado en la base de datos.", registerDto.getUserType());
            throw new InternalServerErrorResponse("Error interno: rol de usuario no configurado.");
        }
        int roleId = roleOpt.get().getId();
        String hashedPassword = PasswordHasher.hashPassword(registerDto.getPassword());

        // --- 4. Crear y guardar el usuario ---
        User newUser = new User();
        newUser.setIdRole(roleId);
        newUser.setFullName(registerDto.getFullName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(hashedPassword);
        User savedUser;
        try {
            savedUser = userRepository.save(newUser);
        } catch (RuntimeException e) {
            log.error("Fallo al guardar el usuario en la base de datos: {}", e.getMessage(), e);
            throw new InternalServerErrorResponse("Error al registrar el usuario.");
        }

        if (savedUser.getId() == 0) {
            throw new InternalServerErrorResponse("Error al registrar el usuario, ID no generado.");
        }

        // --- 5. Manejar el tipo de usuario (Cliente/Proveedor) ---
        Integer companyId = null;
        if (registerDto.getUserType().equalsIgnoreCase("cliente")) {
            try {
                clientRepository.save(savedUser.getId());
            } catch (RuntimeException e) {
                log.error("Fallo al guardar el cliente para el usuario {}: {}", savedUser.getId(), e.getMessage(), e);
                throw new InternalServerErrorResponse("Error al finalizar el registro como cliente.");
            }
        } else if (registerDto.getUserType().equalsIgnoreCase("proveedor")) {
            // MODIFICADO: Los proveedores ahora deben seleccionar una empresa existente
            if (registerDto.getCompanyId() == null || registerDto.getCompanyId() <= 0) {
                throw new BadRequestResponse("Los proveedores deben seleccionar una empresa existente.");
            }
            
            // Verificar que la empresa existe
            Optional<Company> companyOpt = companyRepository.findById(registerDto.getCompanyId());
            if (companyOpt.isEmpty()) {
                throw new BadRequestResponse("La empresa seleccionada no existe.");
            }
            
            companyId = registerDto.getCompanyId();
            
            try {
                providerRepository.save(savedUser.getId(), companyId);
            } catch (RuntimeException e) {
                log.error("Fallo al guardar el proveedor para el usuario {}: {}", savedUser.getId(), e.getMessage(), e);
                throw new InternalServerErrorResponse("Error al finalizar el registro como proveedor.");
            }
        }

        // --- 6. Generar token JWT y DTO de respuesta ---
        String token = jwtManager.generateToken(savedUser, roleOpt.get());
        log.info("Usuario registrado y autenticado: {}", savedUser.getEmail());
        return new AuthResponseDto("Registro exitoso", token, savedUser.getId(), roleOpt.get().getRole(), companyId);
    }

    public AuthResponseDto authenticateUser(UserLoginDto loginDto) {
        // --- 1. Validaciones del DTO ---
        if (loginDto.getEmail() == null || loginDto.getEmail().isEmpty() ||
                loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
            throw new BadRequestResponse("Email y contraseña son obligatorios.");
        }

        // --- 2. Buscar usuario por email ---
        Optional<User> userOpt = userRepository.findByEmail(loginDto.getEmail());
        if (userOpt.isEmpty()) {
            throw new UnauthorizedResponse("Credenciales inválidas.");
        }
        User user = userOpt.get();

        // --- 3. Verificar contraseña ---
        if (!PasswordHasher.checkPassword(loginDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedResponse("Credenciales inválidas.");
        }

        // --- 4. Obtener el rol del usuario ---
        Optional<Role> roleOpt = roleRepository.findById(user.getIdRole());
        if (roleOpt.isEmpty()) {
            log.error("Rol no encontrado para el usuario con ID: {}", user.getId());
            throw new InternalServerErrorResponse("Error interno: rol de usuario no configurado.");
        }
        Role userRole = roleOpt.get();

        // --- 5. Obtener companyId si es proveedor ---
        Integer companyId = null;
        Optional<Provider> providerOpt = providerRepository.findByUserId(user.getId());
        if (providerOpt.isPresent()) {
            Provider provider = providerOpt.get();
            if (provider.getIdCompany() != 0) {
                companyId = provider.getIdCompany();
            }
        }

        // --- 6. Generar token JWT y DTO de respuesta ---
        String token = jwtManager.generateToken(user, userRole);
        log.info("Usuario autenticado: {}", user.getEmail());
        return new AuthResponseDto("Autenticación exitosa", token, user.getId(), userRole.getRole(), companyId);
    }
}