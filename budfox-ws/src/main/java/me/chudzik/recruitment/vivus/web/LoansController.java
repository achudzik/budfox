package me.chudzik.recruitment.vivus.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;

import me.chudzik.recruitment.vivus.model.ErrorMessage;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Joiner;

@RestController
@RequestMapping("/loans")
public class LoansController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoansController.class);

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Loan issueLoan(@RequestBody @Valid LoanApplication application) {
        return null;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public void handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.warn(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.warn(ex.getMessage());
        String details = Joiner.on("\n").join(ex.getBindingResult().getAllErrors());
        return new ErrorMessage(BAD_REQUEST.value(), "Invalid request", details);
    }

}
