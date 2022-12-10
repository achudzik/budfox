package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.LoanNotFoundException;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.repository.LoanRepository;
import io.chudzik.recruitment.budfox.service.impl.LoanServiceImpl;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.util.RetryAnalyzerCount;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.basicConditions;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.client;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.conditionsAfterFirstExtension;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.invalidId;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loan;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanAfterFirstExtension;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanApplication;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.unsavedLoan;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId;

public class LoanServiceTest {

    LoanService sut;

    @Mock LoanRepository loanRepositoryMock;
    @Mock ClientRepository clientRepositoryMock;
    @Mock LoanConditionsService conditionsServiceMock;


    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new LoanServiceImpl(clientRepositoryMock, loanRepositoryMock, conditionsServiceMock);
    }


    @Test(retryAnalyzer = ThreeTimesACharmRetryAnalyzer.class)
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

    // FIXME-ach: workaround for flaky test (works in IDE, fails from time to time from maven execution)
    public static class ThreeTimesACharmRetryAnalyzer extends RetryAnalyzerCount {

        public ThreeTimesACharmRetryAnalyzer() {
            super();
            setCount(3);
        }

        @Override
        public boolean retryMethod(ITestResult result) {
            return super.retry(result);
        }

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
