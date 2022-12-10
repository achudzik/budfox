package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.exception.LoanNotFoundException;
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.ErrorMessage;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.service.ActivityService;
import io.chudzik.recruitment.budfox.service.ClientService;
import io.chudzik.recruitment.budfox.service.LoanService;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Loan issueLoan(@RequestBody @Valid LoanApplication application, HttpServletRequest request)
            throws ClientNotFoundException, RiskyLoanApplicationException {
        clientService.validateClientExistence(application.getClientId());
        activityService.logLoanApplication(application.getClientId(), request);
        riskAssessmentService.validateApplicationSafety(application);
        Loan loan = loanService.issueALoan(application);
        return loan;
    }

    @RequestMapping(value = "{id}", params = "extend=true", method = PUT, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public Loan extendLoan(@PathVariable("id") Long loanId, HttpServletRequest request)
            throws LoanNotFoundException {
        activityService.logLoanExtension(loanId, request);
        Loan loan = loanService.extendLoan(loanId);
        return loan;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.warn(ex.getMessage());
        String details = Joiner.on("\n").join(ex.getBindingResult().getAllErrors());
        return new ErrorMessage(BAD_REQUEST.value(), "Invalid request", details);
    }

    @ExceptionHandler(RiskyLoanApplicationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleRiskyLoanApplicationException(RiskyLoanApplicationException ex) {
        LOGGER.warn(ex.getMessage());
        return new ErrorMessage(BAD_REQUEST.value(), ex.getMessage(), ex.getReason());
    }

    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleLoanNotFoundException(LoanNotFoundException ex) {
        LOGGER.warn(ex.getMessage());
        String details = String.format("Loan ID: %d", ex.getLoanId());
        return new ErrorMessage(NOT_FOUND.value(), ex.getMessage(), details);
    }

}
