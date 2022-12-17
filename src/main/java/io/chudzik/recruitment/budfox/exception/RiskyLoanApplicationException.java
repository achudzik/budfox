package io.chudzik.recruitment.budfox.exception;

import lombok.Getter;

@Getter
public class RiskyLoanApplicationException extends BusinessException {

    private static final long serialVersionUID = 1012157927828281129L;

    private String reason;


    public RiskyLoanApplicationException(String reason) {
        super("Risk associated with loan application is too high.");
        this.reason = reason;
    }

}
