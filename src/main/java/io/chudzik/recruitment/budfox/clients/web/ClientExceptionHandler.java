package io.chudzik.recruitment.budfox.clients.web;

import io.chudzik.recruitment.budfox.clients.dto.ClientException.ClientAlreadyExistsException;
import io.chudzik.recruitment.budfox.clients.dto.ClientException.ClientNotFoundException;
import io.chudzik.recruitment.budfox.commons.web.ErrorMessage;
import io.chudzik.recruitment.budfox.commons.web.GlobalExceptionHandler;
import io.chudzik.recruitment.budfox.loans.web.LoansController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Import(GlobalExceptionHandler.class)
// FIXME-ach: workaround; move somewhere else? for now it waits until more mature code layout
@ControllerAdvice(basePackageClasses = { ClientsController.class, LoansController.class })
@Slf4j
public class ClientExceptionHandler {

    @ResponseBody
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ClientNotFoundException.class)
    public ErrorMessage handleClientNotFoundException(ClientNotFoundException ex) {
        log.warn("Handling ClientNotFoundException [clientId={}, exceptionMessage={}]",
                ex.getClientId(), ex.getMessage());
        return new ErrorMessage(NOT_FOUND, ex.getMessage());
    }


    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ErrorMessage handleClientAlreadyExistsException(ClientAlreadyExistsException ex) {
        log.warn("Handling ClientAlreadyExistsException [clientId={}, exceptionMessage={}]",
                ex.getClientId(), ex.getMessage());
        return new ErrorMessage(BAD_REQUEST, ex.getMessage());
    }

}
