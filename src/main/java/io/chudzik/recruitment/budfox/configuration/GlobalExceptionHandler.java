package io.chudzik.recruitment.budfox.configuration;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.ErrorMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice(basePackageClasses = BudfoxApplication.class)
@Slf4j
public class GlobalExceptionHandler {


    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public void handleIllegalStateException(IllegalStateException ex) {
        log.warn(ex.getMessage());
    }


    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorMessage handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn(ex.getMessage());
        return new ErrorMessage(BAD_REQUEST, ex.getMessage());
    }

}
