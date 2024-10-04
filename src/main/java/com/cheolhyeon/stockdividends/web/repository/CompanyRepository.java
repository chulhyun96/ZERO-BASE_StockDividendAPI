package com.cheolhyeon.stockdividends.web.repository;

import com.cheolhyeon.stockdividends.web.domain.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    Optional<CompanyEntity> findCompanyEntityByTicker(String ticker);
    Optional<CompanyEntity> findCompanyEntityByName(String companyName);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String keyword, PageRequest limit);
}
