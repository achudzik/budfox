package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.ErrorMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHanlder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHanlder.class);

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(BAD_REQUEST)
    public void handleIllegalStateException(IllegalStateException ex) {
        LOGGER.warn(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorMessage handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.warn(ex.getMessage());
        return new ErrorMessage(BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public ErrorMessage handleClientNotFoundException(ClientNotFoundException ex) {
        LOGGER.warn(ex.getMessage());
        String details = String.format("Client ID: %d", ex.getClientId());
        return new ErrorMessage(NOT_FOUND.value(), ex.getMessage(), details);
    }

}
