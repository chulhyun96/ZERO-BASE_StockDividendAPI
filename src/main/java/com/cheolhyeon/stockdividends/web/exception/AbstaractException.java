package com.cheolhyeon.stockdividends.web.exception;

public abstract class AbstaractException extends RuntimeException {
    public abstract int getStatusCode();
    public abstract String getMessage();
}
