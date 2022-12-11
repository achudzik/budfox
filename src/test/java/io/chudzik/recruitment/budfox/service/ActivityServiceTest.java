package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.repository.ActivityRepository;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.service.impl.ActivityServiceImpl;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.LOCAL_IP_ADDRESS;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.client;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.invalidId;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanApplicationActivity;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanExtensionActivity;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId;

public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepositoryMock;
    @Mock
    private ClientRepository clientRepositoryMock;

    private ActivityService sut;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new ActivityServiceImpl(activityRepositoryMock, clientRepositoryMock);
    }


    @Test
    public void shouldPersistInfoAboutApplyingForALoan() {
        // arrange
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        stub(requestMock.getRemoteAddr()).toReturn(LOCAL_IP_ADDRESS);
        doReturn(client()).when(clientRepositoryMock).getOne(validId());

        // act
        sut.logLoanApplication(validId(), requestMock);

        // assert
        verify(activityRepositoryMock).save(loanApplicationActivity());
    }

    @Test
    public void shouldPersistInfoAboutExtendingLoan() {
        // arrange
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        stub(requestMock.getRemoteAddr()).toReturn(LOCAL_IP_ADDRESS);
        doReturn(client()).when(clientRepositoryMock).findByLoansId(invalidId());

        // act
        sut.logLoanExtension(invalidId(), requestMock);

        // assert
        verify(activityRepositoryMock).save(loanExtensionActivity());
    }
}
