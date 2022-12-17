package io.chudzik.recruitment.budfox.loans;

import io.chudzik.recruitment.budfox.clients.Client;
import io.chudzik.recruitment.budfox.clients.ClientService;
import io.chudzik.recruitment.budfox.loans.dto.LoanNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class LoanService {

    private final ClientService clientService;
    private final LoanRepository loanRepository;
    private final LoanConditionsService conditionsService;


    public Loan issueALoan(LoanApplication application) {
        Client client = clientService.getOne(application.getClientId());
        LoanConditions conditions = conditionsService.calculateInitialLoanConditions(application);
        Loan loan = Loan.builder().conditions(conditions).build();
        client.addLoan(loan);
        return loanRepository.save(loan);
    }


    public Loan extendLoan(Long loanId) throws LoanNotFoundException {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));
        LoanConditions newConditions = conditionsService.loanExtensionConditions(loan);
        loan.setCondition(newConditions);
        loan = loanRepository.save(loan);
        return loan;
    }

}
