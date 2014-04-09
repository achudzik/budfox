package me.chudzik.recruitment.vivus.service;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.LOCAL_IP_ADDRESS;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_ACTIVITY;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.repository.ActivityRepository;
import me.chudzik.recruitment.vivus.repository.ClientRepository;
import me.chudzik.recruitment.vivus.service.impl.ActivityServiceImpl;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        doReturn(VALID_CLIENT).when(clientRepositoryMock).findOne(VALID_CLIENT.getId());

        // act
        sut.logLoanApplication(VALID_CLIENT.getId(), requestMock);

        // assert
        verify(activityRepositoryMock).save(VALID_ACTIVITY);
    }

}
