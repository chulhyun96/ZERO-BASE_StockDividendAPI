package com.cheolhyeon.stockdividends.web.service;

import com.cheolhyeon.stockdividends.web.domain.CompanyEntity;
import com.cheolhyeon.stockdividends.web.domain.DividendEntity;
import com.cheolhyeon.stockdividends.web.exception.impl.NoCompanyException;
import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.Dividend;
import com.cheolhyeon.stockdividends.web.model.ScrapedResult;
import com.cheolhyeon.stockdividends.web.model.constants.CacheKey;
import com.cheolhyeon.stockdividends.web.repository.CompanyRepository;
import com.cheolhyeon.stockdividends.web.repository.DividendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        //1. 회사명을 기준으로 회사 정보 조회
        CompanyEntity companyEntity = companyRepository.findCompanyEntityByName(companyName)
                .orElseThrow(NoCompanyException::new);

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository
                .findAllByCompanyId(companyEntity.getId());
        
        //3. 결과 조합 후 반환
        return new ScrapedResult(Company.fromEntity(companyEntity), dividendEntities.stream()
                .map(Dividend::fromEntity)
                .toList());
    }
}
