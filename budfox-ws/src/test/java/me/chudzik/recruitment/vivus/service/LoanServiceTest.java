package me.chudzik.recruitment.vivus.service;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_ID;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN_APPLICATION;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN_CONDITIONS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import me.chudzik.recruitment.vivus.repository.ClientRepository;
import me.chudzik.recruitment.vivus.repository.LoanRepository;
import me.chudzik.recruitment.vivus.service.impl.LoanServiceImpl;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoanServiceTest {

    private LoanService sut;

    @Mock
    private LoanRepository loanRepositoryMock;
    @Mock
    private ClientRepository clientRepositoryMock;
    @Mock
    private LoanConditionsService conditionsServiceMock;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new LoanServiceImpl(clientRepositoryMock, loanRepositoryMock, conditionsServiceMock);
    }

    @Test
    public void shouldOperateOnClientEntityFetchedFromDb() {
        // arrange
        doReturn(VALID_CLIENT).when(clientRepositoryMock).findOne(VALID_ID);
        doReturn(VALID_LOAN_CONDITIONS).when(conditionsServiceMock)
                .calculateInitialLoanConditions(VALID_LOAN_APPLICATION);

        // act
        sut.issueALoan(VALID_LOAN_APPLICATION);

        // assert
        verify(clientRepositoryMock, times(1)).findOne(VALID_ID);
        verifyNoMoreInteractions(clientRepositoryMock);

    }

    @Test
    public void shouldPersistIssuedLoanToDb() {
        // arrange
        doReturn(VALID_CLIENT).when(clientRepositoryMock).findOne(VALID_ID);
        doReturn(VALID_LOAN_CONDITIONS).when(conditionsServiceMock)
                .calculateInitialLoanConditions(VALID_LOAN_APPLICATION);

        // act
        sut.issueALoan(VALID_LOAN_APPLICATION);

        // assert
        verify(loanRepositoryMock, times(1)).save(VALID_LOAN);
        verifyNoMoreInteractions(loanRepositoryMock);
    }

}
