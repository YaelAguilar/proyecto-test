package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import org.example.models.Brand;
import org.example.models.Company;
import org.example.models.CompanyBrand;
import org.example.models.Equipment;
import org.example.models.Provider;
import org.example.models.StateEquipment;
import org.example.models.TypeEquipment;
import org.example.models.User;
import org.example.models.dtos.EquipmentCreateUpdateDto;
import org.example.models.dtos.EquipmentResponseDto;
import org.example.repositories.IBrandRepository;
import org.example.repositories.ICompanyBrandRepository;
import org.example.repositories.ICompanyRepository;
import org.example.repositories.IEquipmentRepository;
import org.example.repositories.IProviderRepository;
import org.example.repositories.IStateEquipmentRepository;
import org.example.repositories.ITypeEquipmentRepository;
import org.example.repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class EquipmentService {
    private static final Logger log = LoggerFactory.getLogger(EquipmentService.class);
    private final IEquipmentRepository equipmentRepository;
    private final IProviderRepository providerRepository;
    private final ITypeEquipmentRepository typeEquipmentRepository;
    private final IStateEquipmentRepository stateEquipmentRepository;
    private final IBrandRepository brandRepository;
    private final IUserRepository userRepository;
    private final ICompanyRepository companyRepository;
    private final ICompanyBrandRepository companyBrandRepository; // NUEVO

    public EquipmentService(IEquipmentRepository equipmentRepository, IProviderRepository providerRepository,
                            ITypeEquipmentRepository typeEquipmentRepository, IStateEquipmentRepository stateEquipmentRepository,
                            IBrandRepository brandRepository, IUserRepository userRepository, ICompanyRepository companyRepository,
                            ICompanyBrandRepository companyBrandRepository) { // MODIFICADO
        this.equipmentRepository = equipmentRepository;
        this.providerRepository = providerRepository;
        this.typeEquipmentRepository = typeEquipmentRepository;
        this.stateEquipmentRepository = stateEquipmentRepository;
        this.brandRepository = brandRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyBrandRepository = companyBrandRepository; // NUEVO
    }

    public List<EquipmentResponseDto> getAllEquipment() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        log.info("Obteniendo todos los equipos. Total: {}", equipmentList.size());
        return equipmentList.stream()
                .map(this::mapEquipmentToDto)
                .collect(Collectors.toList());
    }

    public List<EquipmentResponseDto> getEquipmentByProviderId(int providerId) {
        providerRepository.findById(providerId)
            .orElseThrow(() -> new NotFoundResponse("Proveedor no encontrado con ID: " + providerId));

        List<Equipment> equipmentList = equipmentRepository.findByProviderId(providerId);
        log.info("Obteniendo {} equipos para el proveedor ID: {}", equipmentList.size(), providerId);
        return equipmentList.stream()
                .map(this::mapEquipmentToDto)
                .collect(Collectors.toList());
    }

    // NUEVO: Método para buscar equipos con filtros
    public List<EquipmentResponseDto> searchEquipment(String name, Integer typeId, Integer stateId, 
                                                      Integer companyId, Integer brandId) {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        
        // Aplicar filtros
        List<Equipment> filteredEquipment = allEquipment.stream()
            .filter(eq -> name == null || name.isEmpty() || 
                    eq.getName().toLowerCase().contains(name.toLowerCase()))
            .filter(eq -> typeId == null || eq.getIdTypeEquipment() == typeId)
            .filter(eq -> stateId == null || eq.getIdStateEquipment() == stateId)
            .filter(eq -> brandId == null || eq.getIdBrand() == brandId)
            .filter(eq -> {
                if (companyId == null) return true;
                // Obtener el proveedor y verificar su empresa
                Optional<Provider> provider = providerRepository.findById(eq.getIdProvider());
                return provider.isPresent() && provider.get().getIdCompany() == companyId;
            })
            .collect(Collectors.toList());
        
        log.info("Búsqueda de equipos con filtros - Resultados: {}", filteredEquipment.size());
        return filteredEquipment.stream()
                .map(this::mapEquipmentToDto)
                .collect(Collectors.toList());
    }

    public EquipmentResponseDto getEquipmentById(int id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Equipo no encontrado con ID: " + id));
        log.info("Obteniendo equipo con ID: {}", id);
        return mapEquipmentToDto(equipment);
    }

    public Equipment createEquipment(EquipmentCreateUpdateDto createDto, int authUserId) {
        // 1. Validar DTO
        if (createDto.getName() == null || createDto.getName().trim().isEmpty() ||
            createDto.getPrice() == null || createDto.getPrice().signum() <= 0 ||
            createDto.getTypeId() == 0 || createDto.getStateId() == 0 || createDto.getBrandId() == 0) {
            throw new BadRequestResponse("Los campos obligatorios (nombre, precio, tipo, estado, marca) son requeridos.");
        }

        // 2. Obtener id_provider del usuario autenticado
        Provider provider = providerRepository.findByUserId(authUserId)
                .orElseThrow(() -> new ForbiddenResponse("Solo los proveedores pueden crear equipos."));
        int idProvider = provider.getId();

        // 3. Validar IDs de foreign key
        typeEquipmentRepository.findById(createDto.getTypeId())
                .orElseThrow(() -> new BadRequestResponse("Tipo de equipo inválido con ID: " + createDto.getTypeId()));
        stateEquipmentRepository.findById(createDto.getStateId())
                .orElseThrow(() -> new BadRequestResponse("Estado de equipo inválido con ID: " + createDto.getStateId()));
        brandRepository.findById(createDto.getBrandId())
                .orElseThrow(() -> new BadRequestResponse("Marca inválida con ID: " + createDto.getBrandId()));

        // NUEVO: 4. Validar que la marca pertenece a la empresa del proveedor
        List<CompanyBrand> companyBrands = companyBrandRepository.findByCompanyId(provider.getIdCompany());
        boolean brandBelongsToCompany = companyBrands.stream()
                .anyMatch(cb -> cb.getIdBrand() == createDto.getBrandId());
        
        if (!brandBelongsToCompany) {
            throw new BadRequestResponse("La marca seleccionada no está asociada a tu empresa.");
        }

        // 5. Mapear DTO a entidad Equipment
        Equipment newEquipment = new Equipment();
        newEquipment.setIdProvider(idProvider);
        newEquipment.setIdTypeEquipment(createDto.getTypeId());
        newEquipment.setName(createDto.getName());
        newEquipment.setDescription(createDto.getDescription());
        newEquipment.setPrice(createDto.getPrice());
        newEquipment.setIdStateEquipment(createDto.getStateId());
        newEquipment.setUrlImage(createDto.getUrlImage());
        newEquipment.setIdBrand(createDto.getBrandId());

        log.info("Creando nuevo equipo: {}", newEquipment.getName());
        return equipmentRepository.save(newEquipment);
    }

    public Equipment updateEquipment(int equipmentId, EquipmentCreateUpdateDto updateDto, int authUserId) {
        // 1. Validar DTO
        if (updateDto.getName() == null || updateDto.getName().trim().isEmpty() ||
            updateDto.getPrice() == null || updateDto.getPrice().signum() <= 0 ||
            updateDto.getTypeId() == 0 || updateDto.getStateId() == 0 || updateDto.getBrandId() == 0) {
            throw new BadRequestResponse("Los campos obligatorios (nombre, precio, tipo, estado, marca) son requeridos para la actualización.");
        }

        // 2. Buscar equipo existente
        Equipment existingEquipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundResponse("Equipo no encontrado con ID: " + equipmentId));

        // 3. Verificar que el usuario autenticado es el proveedor del equipo
        Provider provider = providerRepository.findByUserId(authUserId)
                .orElseThrow(() -> new ForbiddenResponse("Solo los proveedores pueden actualizar sus equipos."));
        if (existingEquipment.getIdProvider() != provider.getId()) {
            throw new ForbiddenResponse("No tienes permiso para actualizar este equipo.");
        }

        // 4. Validar IDs de foreign key
        typeEquipmentRepository.findById(updateDto.getTypeId())
                .orElseThrow(() -> new BadRequestResponse("Tipo de equipo inválido con ID: " + updateDto.getTypeId()));
        stateEquipmentRepository.findById(updateDto.getStateId())
                .orElseThrow(() -> new BadRequestResponse("Estado de equipo inválido con ID: " + updateDto.getStateId()));
        brandRepository.findById(updateDto.getBrandId())
                .orElseThrow(() -> new BadRequestResponse("Marca inválida con ID: " + updateDto.getBrandId()));

        // NUEVO: 5. Validar que la marca pertenece a la empresa del proveedor
        List<CompanyBrand> companyBrands = companyBrandRepository.findByCompanyId(provider.getIdCompany());
        boolean brandBelongsToCompany = companyBrands.stream()
                .anyMatch(cb -> cb.getIdBrand() == updateDto.getBrandId());
        
        if (!brandBelongsToCompany) {
            throw new BadRequestResponse("La marca seleccionada no está asociada a tu empresa.");
        }

        // 6. Actualizar campos de la entidad
        existingEquipment.setIdTypeEquipment(updateDto.getTypeId());
        existingEquipment.setName(updateDto.getName());
        existingEquipment.setDescription(updateDto.getDescription());
        existingEquipment.setPrice(updateDto.getPrice());
        existingEquipment.setIdStateEquipment(updateDto.getStateId());
        existingEquipment.setUrlImage(updateDto.getUrlImage());
        existingEquipment.setIdBrand(updateDto.getBrandId());

        log.info("Actualizando equipo ID {}: {}", equipmentId, existingEquipment.getName());
        return equipmentRepository.update(existingEquipment);
    }

    public void deleteEquipment(int equipmentId, int authUserId) {
        Equipment existingEquipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new NotFoundResponse("Equipo no encontrado con ID: " + equipmentId));

        Provider provider = providerRepository.findByUserId(authUserId)
                .orElseThrow(() -> new ForbiddenResponse("Solo los proveedores pueden eliminar sus equipos."));
        if (existingEquipment.getIdProvider() != provider.getId()) {
            throw new ForbiddenResponse("No tienes permiso para eliminar este equipo.");
        }

        log.info("Eliminando equipo con ID: {}", equipmentId);
        equipmentRepository.delete(equipmentId);
    }

    private EquipmentResponseDto mapEquipmentToDto(Equipment equipment) {
        AtomicReference<String> providerNameRef = new AtomicReference<>("N/A");
        AtomicReference<String> companyNameRef = new AtomicReference<>("N/A");

        Optional<Provider> providerOpt = providerRepository.findById(equipment.getIdProvider());
        if (providerOpt.isPresent()) {
            Provider provider = providerOpt.get();
            Optional<User> userOpt = userRepository.findById(provider.getIdUser());
            userOpt.ifPresent(user -> providerNameRef.set(user.getFullName()));

            if (provider.getIdCompany() > 0) {
                Optional<Company> companyOpt = companyRepository.findById(provider.getIdCompany());
                companyOpt.ifPresent(company -> companyNameRef.set(company.getName()));
            }
        }

        String typeName = typeEquipmentRepository.findById(equipment.getIdTypeEquipment())
                .map(TypeEquipment::getType)
                .orElse("Desconocido");

        String stateName = stateEquipmentRepository.findById(equipment.getIdStateEquipment())
                .map(StateEquipment::getState)
                .orElse("Desconocido");

        String brandName = brandRepository.findById(equipment.getIdBrand())
                .map(Brand::getNameBrand)
                .orElse("Desconocida");

        return new EquipmentResponseDto(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getPrice(),
                equipment.getUrlImage(),
                equipment.getCreatedAt(),
                providerNameRef.get(),
                companyNameRef.get(),
                typeName,
                stateName,
                brandName
        );
    }
}