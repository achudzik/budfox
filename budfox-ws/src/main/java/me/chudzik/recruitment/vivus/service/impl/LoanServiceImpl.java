package me.chudzik.recruitment.vivus.service.impl;

import me.chudzik.recruitment.vivus.exception.LoanNotFoundException;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.model.LoanConditions;
import me.chudzik.recruitment.vivus.repository.ClientRepository;
import me.chudzik.recruitment.vivus.repository.LoanRepository;
import me.chudzik.recruitment.vivus.service.LoanConditionsService;
import me.chudzik.recruitment.vivus.service.LoanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private ClientRepository clientRepository;
    private LoanRepository loanRepository;
    private LoanConditionsService conditionsService;

    @Autowired
    public LoanServiceImpl(ClientRepository clientRepository, LoanRepository loanRepository, LoanConditionsService conditionsService) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
        this.conditionsService = conditionsService;
    }

    @Override
    public Loan issueALoan(LoanApplication application) {
        Client client = clientRepository.findOne(application.getClientId());
        LoanConditions conditions = conditionsService.calculateInitialLoanConditions(application);
        Loan loan = Loan.builder().conditions(conditions).build();
        client.addLoan(loan);
        return loanRepository.save(loan);
    }

    @Override
    public Loan extendLoan(Long loanId) throws LoanNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
