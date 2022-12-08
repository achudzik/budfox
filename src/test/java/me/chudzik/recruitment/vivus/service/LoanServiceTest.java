package me.chudzik.recruitment.vivus.service;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.basicConditions;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.client;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.conditionsAfterFirstExtension;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.invalidId;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.loan;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.loanAfterFirstExtension;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.loanApplication;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.unsavedLoan;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.validId;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import me.chudzik.recruitment.vivus.exception.LoanNotFoundException;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;
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
        doReturn(client()).when(clientRepositoryMock).findOne(validId());
        doReturn(basicConditions()).when(conditionsServiceMock)
                .calculateInitialLoanConditions(loanApplication());

        // act
        sut.issueALoan(loanApplication());

        // assert
        verify(clientRepositoryMock, times(1)).findOne(validId());
        verifyNoMoreInteractions(clientRepositoryMock);

    }

    @Test
    public void shouldPersistIssuedLoanToDb() {
        // arrange
        LoanApplication application = loanApplication();
        doReturn(client()).when(clientRepositoryMock).findOne(validId());
        doReturn(basicConditions()).when(conditionsServiceMock)
                .calculateInitialLoanConditions(application);

        // act
        sut.issueALoan(application);

        // assert
        verify(loanRepositoryMock, times(1)).save(unsavedLoan());
        verifyNoMoreInteractions(loanRepositoryMock);
    }

    @Test
    public void shouldPersistExtendedLoansToDb() {
        // arrange
        Loan loan = loan();
        doReturn(loan).when(loanRepositoryMock).findOne(validId());
        doReturn(conditionsAfterFirstExtension()).when(conditionsServiceMock)
                .loanExtensionConditions(loan);

        // act
        sut.extendLoan(validId());

        // assert
        verify(loanRepositoryMock, times(1)).findOne(validId());
        verify(loanRepositoryMock, times(1)).save(loanAfterFirstExtension());
        verifyNoMoreInteractions(loanRepositoryMock);
    }

    @Test(expectedExceptions = LoanNotFoundException.class,
            expectedExceptionsMessageRegExp = "Loan with given ID not found.")
    public void shouldThrowExceptionOnExtendedLoansToDb() {
        // arrange
        doReturn(null).when(loanRepositoryMock).findOne(invalidId());

        // act
        sut.extendLoan(invalidId());

        // assert
        verify(loanRepositoryMock, times(1)).findOne(invalidId());
        verifyNoMoreInteractions(loanRepositoryMock);
    }

}
