package me.chudzik.recruitment.vivus.service;

import static me.chudzik.recruitment.vivus.model.Activity.ActivityType.LOAN_APPLICATION;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.LOCAL_IP_ADDRESS;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.model.Activity;
import me.chudzik.recruitment.vivus.repository.ActivityRepository;
import me.chudzik.recruitment.vivus.service.impl.ActivityServiceImpl;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepositoryMock;

    private ActivityService sut;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new ActivityServiceImpl(activityRepositoryMock);
    }

    @Test
    public void shouldPersistInfoAboutApplyingForALoan() {
        // arrange
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        stub(requestMock.getRemoteAddr()).toReturn(LOCAL_IP_ADDRESS);

        Activity loanApplicationActivity = Activity.builder()
                .client(VALID_CLIENT)
                .type(LOAN_APPLICATION)
                .ipAddress(LOCAL_IP_ADDRESS)
                .build();

        // act
        sut.logLoanApplication(VALID_CLIENT.getId(), requestMock);

        // assert
        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepositoryMock).save(captor.capture());
        assertThat(captor.getValue()).isLenientEqualsToByIgnoringFields(loanApplicationActivity, "eventTime");
    }

}
