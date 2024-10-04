package com.cheolhyeon.stockdividends;

import com.cheolhyeon.stockdividends.web.config.CacheConfig;
import com.cheolhyeon.stockdividends.web.model.Company;
import com.cheolhyeon.stockdividends.web.model.ScrapedResult;
import com.cheolhyeon.stockdividends.web.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class StockDividendsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockDividendsApplication.class, args);

    }
}
