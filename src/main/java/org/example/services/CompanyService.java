package org.example.services;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import org.example.models.Brand;
import org.example.models.Company;
import org.example.models.CompanyBrand;
import org.example.repositories.IBrandRepository;
import org.example.repositories.ICompanyBrandRepository;
import org.example.repositories.ICompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompanyService {
    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
    private final ICompanyRepository companyRepository;
    private final IBrandRepository brandRepository;
    private final ICompanyBrandRepository companyBrandRepository;

    public CompanyService(ICompanyRepository companyRepository, IBrandRepository brandRepository,
                          ICompanyBrandRepository companyBrandRepository) {
        this.companyRepository = companyRepository;
        this.brandRepository = brandRepository;
        this.companyBrandRepository = companyBrandRepository;
    }

    public List<Company> getAllCompanies() {
        log.info("Obteniendo todas las compañías.");
        return companyRepository.findAll();
    }

    public Company getCompanyById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + id));
    }

    // MODIFICADO: Solo administradores pueden crear empresas
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

    // MODIFICADO: Solo administradores pueden actualizar empresas
    public Company updateCompany(int id, Company updatedCompany) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + id));

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

    // MODIFICADO: Solo administradores pueden eliminar empresas
    public void deleteCompany(int id) {
        companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + id));

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

    // MODIFICADO: Solo administradores pueden asociar marcas a empresas
    public void addBrandToCompany(int companyId, int brandId) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + companyId));
        brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundResponse("Marca no encontrada con ID: " + brandId));

        if (companyBrandRepository.findByCompanyAndBrand(companyId, brandId).isPresent()) {
            throw new BadRequestResponse("La marca ya está asociada a esta compañía.");
        }

        log.info("Asociando marca ID {} a compañía ID {}", brandId, companyId);
        companyBrandRepository.save(companyId, brandId);
    }

    // MODIFICADO: Solo administradores pueden desasociar marcas de empresas
    public void removeBrandFromCompany(int companyId, int brandId) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundResponse("Compañía no encontrada con ID: " + companyId));
        brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundResponse("Marca no encontrada con ID: " + brandId));

        Optional<CompanyBrand> association = companyBrandRepository.findByCompanyAndBrand(companyId, brandId);
        if (association.isEmpty()) {
            throw new NotFoundResponse("La marca no está asociada a esta compañía.");
        }

        log.info("Desasociando marca ID {} de compañía ID {}", brandId, companyId);
        companyBrandRepository.deleteByCompanyAndBrand(companyId, brandId);
    }
}