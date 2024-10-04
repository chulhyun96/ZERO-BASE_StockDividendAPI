package com.cheolhyeon.stockdividends.web.service;

import com.cheolhyeon.stockdividends.web.domain.CompanyEntity;
import com.cheolhyeon.stockdividends.web.domain.DividendEntity;
import com.cheolhyeon.stockdividends.web.exception.impl.NoCompanyException;
import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.ScrapedResult;
import com.cheolhyeon.stockdividends.web.repository.CompanyRepository;
import com.cheolhyeon.stockdividends.web.repository.DividendRepository;
import com.cheolhyeon.stockdividends.web.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CompanyService {

    private final Trie trie;

    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Transactional
    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("Company already exists -> " + ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("Company not found ->" + ticker);
        }
        // 해당 회사가 존재할 경우 회상의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);
        CompanyEntity companyEntity = companyRepository.save(CompanyEntity.fromDTO(company));
        dividendRepository.saveAll(scrapedResult.getDividends().stream()
                .map(e -> DividendEntity.fromDTO(companyEntity.getId(), e))
                .toList());
        return company;
    }

    @Transactional(readOnly = true)
    public Company getCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findCompanyEntityByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("Company not found -> " + ticker));
        return Company.fromEntity(companyEntity);
    }

    @Transactional(readOnly = true)
    public Page<Company> getAllCompany(Pageable pageable) {
        Page<CompanyEntity> companyEntityList = companyRepository.findAll(pageable);
        if (companyEntityList.isEmpty()) {
            throw new RuntimeException("There is nothing in DB");
        }
        return companyEntityList.map(Company::fromEntity);
    }

    /**
     * 자동완성 기능에 사용될 키워드를 Trie에 추가
     * Trie는 문자열을 키로 저장하고 각 키 저장시 노드에 추가됨.
     * 자동완성 목록에 새로운 키워드를 추가하는 역할, apple, application, app과 같은 키워드 검색 시
     * 그 키워드를 이 메서드를 통해 트리에 추가
     * @param keyword
     */
    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    /**
     * 입력된 키워드로 시작하는 모든 단어를 자동완성으로 제공하는 역할
     * prefixMap은 접두사 검색을 통해 주어진 키워드로 시작하는 모든 키를 반환
     * 반환되는 형태는 Map의 형태
     * @param keyword
     * @return
     */
    public List<String> autocomplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword)
                .keySet()
                .stream()
                .limit(5)
                .toList();
    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        PageRequest limit = PageRequest.of(0, 5);
        Page<CompanyEntity> companyEntities
                = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(c -> c.getName())
                .toList();
    }
    @Transactional
    public String deleteCompany(String companyName) {
        CompanyEntity companyEntity = companyRepository.findCompanyEntityByTicker(companyName)
                .orElseThrow(NoCompanyException::new);
        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);
        deleteAutocompleteKeyword(companyName);
        return companyEntity.getName();
    }
}
