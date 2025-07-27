package org.example.repositories;

import org.example.models.CompanyBrand;

import java.util.List;
import java.util.Optional;

public interface ICompanyBrandRepository {
    CompanyBrand save(int companyId, int brandId);
    void delete(int id);
    void deleteByCompanyAndBrand(int companyId, int brandId);
    List<CompanyBrand> findByCompanyId(int companyId);
    List<CompanyBrand> findByBrandId(int brandId);
    Optional<CompanyBrand> findByCompanyAndBrand(int companyId, int brandId);
}