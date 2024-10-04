package com.cheolhyeon.stockdividends.web.exception.impl;

import com.cheolhyeon.stockdividends.web.exception.AbstaractException;
import org.springframework.http.HttpStatus;

public class NoCompanyException extends AbstaractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 회사명입니다.";
    }
}
