package com.cheolhyeon.stockdividends.web.controller;

import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.constants.CacheKey;
import com.cheolhyeon.stockdividends.web.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager cacheManager;

    /*회사 검색시 자동완성 기능*/
    @GetMapping("/company/autocomplete")
    public ResponseEntity<?> autocomplete (@RequestParam String keyword) {
        List<String> result = companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    /*회사 검색*/
    @GetMapping("/company")
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(@RequestParam String ticker) {
        return ResponseEntity.ok(companyService.getCompany(ticker));
    }

    /*현재 DB에 등록되어있는 모든 Company 보여주기*/
    @GetMapping("/companies")
    public ResponseEntity<?> searchAllCompany(final Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompany(pageable));
    }

    /*회사 등록*/
    @PostMapping("/company")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        if (ObjectUtils.isEmpty(request.getTicker())) {
           throw new RuntimeException("Ticker cannot be empty -> " + request.getTicker());
        }
        Company company = companyService.save(request.getTicker());
        companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    /*회사 삭제*/
    @DeleteMapping("/company/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompany(ticker);
        // Redis에서 캐쉬 데이터 삭제
        clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    private void clearFinanceCache(String companyName) {
        Objects.requireNonNull(cacheManager.getCache(CacheKey.KEY_FINANCE)).evict(companyName);
    }
}
