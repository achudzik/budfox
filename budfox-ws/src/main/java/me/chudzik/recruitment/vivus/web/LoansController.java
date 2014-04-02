package me.chudzik.recruitment.vivus.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
public class LoansController {

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Loan issueLoan(@RequestBody LoanApplication application) {
        return null;
    }

}
