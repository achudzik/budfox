package io.chudzik.recruitment.budfox.exception;

import lombok.Getter;

@Getter
public class LoanNotFoundException extends BusinessException {

    private static final long serialVersionUID = 2174137145768641999L;

    private final Long loanId;


    public LoanNotFoundException(Long loanId) {
        super("Loan with given ID not found.");
        this.loanId = loanId;
    }

}
