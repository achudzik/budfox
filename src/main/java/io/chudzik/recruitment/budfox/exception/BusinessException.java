package io.chudzik.recruitment.budfox.exception;

public abstract class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
