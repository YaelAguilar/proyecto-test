package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.InternalServerErrorResponse;
import org.example.models.Brand;
import org.example.models.Company;
import org.example.models.CompanyBrand;
import org.example.models.Provider;
import org.example.models.User;
import org.example.repositories.IBrandRepository;
import org.example.repositories.ICompanyBrandRepository; // Nueva inyección
import org.example.repositories.ICompanyRepository;
import org.example.repositories.IProviderRepository;
import org.example.repositories.IUserRepository; // Puede necesitarse para obtener info del proveedor
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompanyService {
    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
    private final ICompanyRepository companyRepository;
    private final IBrandRepository brandRepository;
    private final IProviderRepository providerRepository;
    private final ICompanyBrandRepository companyBrandRepository; // Nueva inyección
    private final IUserRepository userRepository; // Necesario para detalles del proveedor

    public CompanyService(ICompanyRepository companyRepository, IBrandRepository brandRepository,
                          IProviderRepository providerRepository, ICompanyBrandRepository companyBrandRepository,
                          IUserRepository userRepository) { // Añadir a constructor
        this.companyRepository = companyRepository;
        this.brandRepository = brandRepository;
        this.providerRepository = providerRepository;
        this.companyBrandRepository = companyBrandRepository;
        this.userRepository = userRepository;
    }

    public List<Company> getAllCompanies() {
        log.info("Obteniendo todas las compañías.");
        return companyRepository.findAll(); // Asume findAll en ICompanyRepository
    }

    public Company getCompanyById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + id));
    }

    public Company createCompany(Company company) {
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre de la compañía es obligatorio.");
        }
        if (companyRepository.findByName(company.getName()).isPresent()) {
            throw new BadRequestResponse("Ya existe una compañía con el nombre: " + company.getName());
        }
        log.info("Creando nueva compañía: {}", company.getName());
        return companyRepository.save(company);
    }

    public Company updateCompany(int id, Company updatedCompany, int authUserId) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + id));

        // Verificar que el usuario autenticado sea un proveedor de esta compañía
        Optional<Provider> providerOpt = providerRepository.findByUserId(authUserId);
        if (providerOpt.isEmpty() || providerOpt.get().getIdCompany() != id) {
            throw new BadRequestResponse("No tienes permisos para actualizar esta compañía.");
        }

        if (updatedCompany.getName() == null || updatedCompany.getName().trim().isEmpty()) {
            throw new BadRequestResponse("El nombre de la compañía es obligatorio.");
        }
        Optional<Company> companyWithName = companyRepository.findByName(updatedCompany.getName());
        if (companyWithName.isPresent() && companyWithName.get().getId() != id) {
            throw new BadRequestResponse("Ya existe otra compañía con el nombre: " + updatedCompany.getName());
        }

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setEmail(updatedCompany.getEmail());
        existingCompany.setPhone(updatedCompany.getPhone());
        existingCompany.setAddress(updatedCompany.getAddress());
        existingCompany.setWebSite(updatedCompany.getWebSite());

        log.info("Actualizando compañía ID {}: {}", id, updatedCompany.getName());
        return companyRepository.update(existingCompany);
    }

    public void deleteCompany(int id, int authUserId) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + id));

        // Verificar que el usuario autenticado sea un proveedor de esta compañía y que no haya otros proveedores
        // O si quieres que solo un admin pueda eliminar compañías. Por ahora, un proveedor de la compañía.
        Optional<Provider> providerOpt = providerRepository.findByUserId(authUserId);
        if (providerOpt.isEmpty() || providerOpt.get().getIdCompany() != id) {
            throw new BadRequestResponse("No tienes permisos para eliminar esta compañía.");
        }
        // Lógica adicional: ¿Qué pasa si la compañía tiene equipos publicados?
        // La BD con ON DELETE RESTRICT en providers.id_company > company.id lo impedirá por defecto si hay proveedores.
        log.info("Eliminando compañía con ID: {}", id);
        companyRepository.delete(id);
    }

    public List<Brand> getBrandsByCompany(int companyId) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + companyId));

        List<CompanyBrand> associations = companyBrandRepository.findByCompanyId(companyId);
        return associations.stream()
                .map(cb -> brandRepository.findById(cb.getIdBrand()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void addBrandToCompany(int companyId, int brandId, int authUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + companyId));
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundResponse("Marca no encontrada con ID: " + brandId));

        // Verificar permisos: solo un proveedor de la compañía puede añadir marcas
        Optional<Provider> providerOpt = providerRepository.findByUserId(authUserId);
        if (providerOpt.isEmpty() || providerOpt.get().getIdCompany() != companyId) {
            throw new BadRequestResponse("No tienes permisos para asociar marcas a esta compañía.");
        }

        if (companyBrandRepository.findByCompanyAndBrand(companyId, brandId).isPresent()) {
            throw new BadRequestResponse("La marca ya está asociada a esta compañía.");
        }

        log.info("Asociando marca ID {} a compañía ID {}", brandId, companyId);
        companyBrandRepository.save(companyId, brandId);
    }

    public void removeBrandFromCompany(int companyId, int brandId, int authUserId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + companyId));
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundResponse("Marca no encontrada con ID: " + brandId));

        // Verificar permisos
        Optional<Provider> providerOpt = providerRepository.findByUserId(authUserId);
        if (providerOpt.isEmpty() || providerOpt.get().getIdCompany() != companyId) {
            throw new BadRequestResponse("No tienes permisos para desasociar marcas de esta compañía.");
        }

        Optional<CompanyBrand> association = companyBrandRepository.findByCompanyAndBrand(companyId, brandId);
        if (association.isEmpty()) {
            throw new NotFoundResponse("La marca no está asociada a esta compañía.");
        }

        log.info("Desasociando marca ID {} de compañía ID {}", brandId, companyId);
        companyBrandRepository.deleteByCompanyAndBrand(companyId, brandId);
    }
}