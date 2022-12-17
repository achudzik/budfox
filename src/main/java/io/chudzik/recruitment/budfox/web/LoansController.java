package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.exception.ClientException.ClientNotFoundException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

@RequestMapping(path = "/loans", produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
@RequiredArgsConstructor
public class LoansController {

    private final ActivityService activityService;
    private final ClientService clientService;
    private final LoanService loanService;
    private final RiskAssessmentService riskAssessmentService;


    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Loan issueLoan(@RequestBody @Valid LoanApplication application, HttpServletRequest request)
            throws ClientNotFoundException, RiskyLoanApplicationException {
        clientService.validateClientExistence(application.getClientId());
        activityService.logLoanApplication(application.getClientId(), request);
        riskAssessmentService.validateApplicationSafety(application);
        Loan loan = loanService.issueALoan(application);
        return loan;
    }

    @ResponseStatus(OK)
    @PutMapping(value = "/{id}", params = "extend=true")
    public Loan extendLoan(@PathVariable("id") Long loanId, HttpServletRequest request)
            throws LoanNotFoundException {
        activityService.logLoanExtension(loanId, request);
        Loan loan = loanService.extendLoan(loanId);
        return loan;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Handling MethodArgumentNotValidException exception", ex);
        String details = Joiner.on("\n").join(ex.getBindingResult().getAllErrors());
        return new ErrorMessage(BAD_REQUEST, "Invalid request", details);
    }


    @ExceptionHandler(RiskyLoanApplicationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleRiskyLoanApplicationException(RiskyLoanApplicationException ex) {
        log.warn("Handling RiskyLoanApplicationException exception", ex);
        return new ErrorMessage(BAD_REQUEST, ex.getMessage(), ex.getReason());
    }


    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleLoanNotFoundException(LoanNotFoundException ex) {
        log.warn("Handling LoanNotFoundException exception", ex);
        String details = String.format("Loan ID: %d", ex.getLoanId());
        return new ErrorMessage(NOT_FOUND, ex.getMessage(), details);
    }

}
