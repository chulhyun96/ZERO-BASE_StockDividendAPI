package com.cheolhyeon.stockdividends.web.scheduler;

import com.cheolhyeon.stockdividends.web.domain.CompanyEntity;
import com.cheolhyeon.stockdividends.web.domain.DividendEntity;
import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.ScrapedResult;
import com.cheolhyeon.stockdividends.web.model.constants.CacheKey;
import com.cheolhyeon.stockdividends.web.repository.CompanyRepository;
import com.cheolhyeon.stockdividends.web.repository.DividendRepository;
import com.cheolhyeon.stockdividends.web.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooScraper;
    private final DividendRepository dividendRepository;


    /**
     * 일정 주기마다 수행
     * scheduled 애노테이션은 배당금 정보가 업데이트 될 경우 DB도 업데이트 해야되기에 설정한것.
     * ChacheEvict 애노테이션을 통해서 스케쥴링이 실행 될 때 어차피 디비도 업데이트 되기 때문에
     * Redis 서버의 캐시도 업데이트 해줘야 되서 캐시 모두 삭제
     */
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {

        // 저장된 회사 목록 조회
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        if (companyEntities.isEmpty()) {
            throw new RuntimeException("There is Nothing in DB");
        }
        // 회사마다 배당금 정보를 새로 스크래핑
        for (CompanyEntity company : companyEntities) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = yahooScraper.scrap(Company.fromEntity(company));
            // 스프래핑한 배당금 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    .map(dividend -> DividendEntity.fromDTO(company.getId(), dividend))
                    .forEach(dividend -> {
                        boolean exists = dividendRepository.existsByCompanyIdAndDate(dividend.getCompanyId(), dividend.getDate());
                        if (!exists) {
                            dividendRepository.save(dividend);
                            log.info("scraping scheduler is Saving -> " + company.getName());
                        }
                    });
            log.info("scraping scheduler is Finished -> " + company.getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
