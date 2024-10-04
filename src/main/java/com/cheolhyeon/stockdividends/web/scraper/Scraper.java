package com.cheolhyeon.stockdividends.web.scraper;

import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.ScrapedResult;

// 나중에 Yahoo가 아닌 다른 사이트를 이용해서 Scrap을 할 수 있기 때문에
public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
