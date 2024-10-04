package com.cheolhyeon.stockdividends.web.scraper;

import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.Dividend;
import com.cheolhyeon.stockdividends.web.model.ScrapedResult;
import com.cheolhyeon.stockdividends.web.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String STATICS_URL = "https://finance.yahoo.com/quote/%s/history/?frequency=1mo&period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000; // ms -> /1000을 함으로써 -> s로 해줌
            // Yahoo Finance 페이지에 연결
            String url = String.format(STATICS_URL, company.getTicker(), START_TIME, now);
            Connection connect = Jsoup.connect(url);
            Document document = connect.get();

            // 테이블과 tbody를 선택하고 바로 tr 순회
            Element tbody = document.getElementsByTag("table").get(0).getElementsByTag("tbody").get(0);

            // 각 행에 대해서 처리
            List<Dividend> dividends = new ArrayList<>();

            for (Element row : tbody.getElementsByTag("tr")) {
                String eleDate = row.getElementsByTag("td").get(0).text(); // 첫 번째 열 (날짜)
                String dividendText = " " + row.getElementsByTag("td").get(1).text(); // 두 번째 열 (Dividend 정보)
                String data = eleDate + dividendText;
                if (!dividendText.contains("Dividend")) {
                    continue;
                }
                String[] split = data.split(" ");
                int month = Month.strToNumber(split[0]);// month
                int day = Integer.parseInt(split[1].replace(",", "")); // day
                int year = Integer.parseInt(split[2]); // year
                String dividend = split[3];

                if (month < 0) {
                    throw new IllegalArgumentException("Invalid month -> " + month);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
            }
            scrapedResult.setDividends(dividends);
        } catch (IOException e) {
            // TODO : exception handling
            throw new RuntimeException(e);
        }
        return scrapedResult;
    }
    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);
        try {
            Document document = Jsoup.connect(url).get();
            String h1 = document.getElementsByTag("H1").get(1).text();
            String title = h1.split("\\(")[0].trim();
            return Company.builder()
                    .name(title)
                    .ticker(ticker)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
