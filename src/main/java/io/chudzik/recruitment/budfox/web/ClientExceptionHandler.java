package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.configuration.GlobalExceptionHandler;
import io.chudzik.recruitment.budfox.exception.ClientException.ClientAlreadyExistsException;
import io.chudzik.recruitment.budfox.exception.ClientException.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.ErrorMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Import(GlobalExceptionHandler.class)
@ControllerAdvice(basePackageClasses = ClientExceptionHandler.class)
class ClientExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientExceptionHandler.class);


    @ResponseBody
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ClientNotFoundException.class)
    public ErrorMessage handleClientNotFoundException(ClientNotFoundException ex) {
        LOGGER.warn("Handling ClientNotFoundException [clientId={}, exceptionMessage={}]",
                ex.getClientId(), ex.getMessage());
        return new ErrorMessage(NOT_FOUND, ex.getMessage());
    }


    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ErrorMessage handleClientAlreadyExistsException(ClientAlreadyExistsException ex) {
        LOGGER.warn("Handling ClientAlreadyExistsException [clientId={}, exceptionMessage={}]",
                ex.getClientId(), ex.getMessage());
        return new ErrorMessage(BAD_REQUEST, ex.getMessage());
    }

}
