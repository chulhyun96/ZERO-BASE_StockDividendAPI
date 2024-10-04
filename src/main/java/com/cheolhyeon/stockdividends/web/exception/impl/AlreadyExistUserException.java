package com.cheolhyeon.stockdividends.web.exception.impl;

import com.cheolhyeon.stockdividends.web.exception.AbstaractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistUserException extends AbstaractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 사용자명입니다.";
    }
}
