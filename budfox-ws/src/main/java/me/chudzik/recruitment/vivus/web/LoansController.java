package me.chudzik.recruitment.vivus.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import me.chudzik.recruitment.vivus.exception.ClientNotFoundException;
import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.ErrorMessage;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.service.ActivityService;
import me.chudzik.recruitment.vivus.service.ClientService;
import me.chudzik.recruitment.vivus.service.LoanService;
import me.chudzik.recruitment.vivus.service.RiskAssessmentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ActivityService activityService;
    private ClientService clientService;
    private LoanService loanService;
    private RiskAssessmentService riskAssessmentService;

    @Autowired
    public LoansController(ActivityService activityService,
            ClientService clientService,
            LoanService loanService,
            RiskAssessmentService riskAssessmentService) {
        this.activityService = activityService;
        this.clientService = clientService;
        this.loanService = loanService;
        this.riskAssessmentService = riskAssessmentService;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Loan issueLoan(@RequestBody @Valid LoanApplication application, HttpServletRequest request)
            throws ClientNotFoundException, RiskyLoanApplicationException {
        clientService.validateClientExistence(application.getClientId());
        activityService.logLoanApplication(application.getClientId(), request);
        riskAssessmentService.validateApplicationSafety(application);
        Loan loan = loanService.issueALoan(application);
        return loan;
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

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleClientNotFoundException(ClientNotFoundException ex) {
        LOGGER.warn(ex.getMessage());
        String details = String.format("Client ID: %d", ex.getClientId());
        return new ErrorMessage(NOT_FOUND.value(), ex.getMessage(), details);
    }

    @ExceptionHandler(RiskyLoanApplicationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleRiskyLoanApplicationException(RiskyLoanApplicationException ex) {
        LOGGER.warn(ex.getMessage());
        return new ErrorMessage(BAD_REQUEST.value(), ex.getMessage(), ex.getReason());
    }

}
