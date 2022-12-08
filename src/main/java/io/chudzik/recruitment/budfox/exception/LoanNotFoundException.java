package io.chudzik.recruitment.budfox.exception;

public class LoanNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2174137145768641999L;

    private Long loanId;

    public LoanNotFoundException(Long loanId) {
        super("Loan with given ID not found.");
        this.loanId = loanId;
    }

    public Long getLoanId() {
        return loanId;
    }

}
