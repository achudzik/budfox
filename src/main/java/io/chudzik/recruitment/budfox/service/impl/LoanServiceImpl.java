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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class LoanServiceImpl implements LoanService {

    ClientRepository clientRepository;
    LoanRepository loanRepository;
    LoanConditionsService conditionsService;


    @Autowired
    public LoanServiceImpl(ClientRepository clientRepository, LoanRepository loanRepository, LoanConditionsService conditionsService) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
        this.conditionsService = conditionsService;
    }


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
