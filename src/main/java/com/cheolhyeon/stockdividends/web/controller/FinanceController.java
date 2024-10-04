package com.cheolhyeon.stockdividends.web.controller;

import com.cheolhyeon.stockdividends.web.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    /*검색한 회사의 배당금 조회*/
    @GetMapping("/finance/dividend/{companyName}")
    public ResponseEntity<?> searchFinanceByCompanyName(@PathVariable String companyName) {
        return ResponseEntity.ok(financeService.getDividendByCompanyName(companyName));
    }
}
