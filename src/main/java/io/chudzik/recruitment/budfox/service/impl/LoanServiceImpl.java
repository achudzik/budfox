package io.chudzik.recruitment.budfox.service.impl;

import io.chudzik.recruitment.budfox.exception.LoanNotFoundException;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.model.LoanConditions;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.repository.LoanRepository;
import io.chudzik.recruitment.budfox.service.LoanConditionsService;
import io.chudzik.recruitment.budfox.service.LoanService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;
    private final LoanConditionsService conditionsService;


    @Override
    public Loan issueALoan(LoanApplication application) {
        Client client = clientRepository.getOne(application.getClientId());
        LoanConditions conditions = conditionsService.calculateInitialLoanConditions(application);
        Loan loan = Loan.builder().conditions(conditions).build();
        client.addLoan(loan);
        return loanRepository.save(loan);
    }


    @Override
    public Loan extendLoan(Long loanId) throws LoanNotFoundException {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new LoanNotFoundException(loanId));
        LoanConditions newConditions = conditionsService.loanExtensionConditions(loan);
        loan.setCondition(newConditions);
        loan = loanRepository.save(loan);
        return loan;
    }

}
