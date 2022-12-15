package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.Activity;
import io.chudzik.recruitment.budfox.repository.ActivityRepository;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService;

import org.fest.assertions.api.Assertions;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION;

public class DailyApplicationLimitRiskEvaluatorTest {

    static final Integer DAILY_APPLICATION_LIMIT = 2;

    @Mock ActivityRepository activityRepositoryMock;
    @Mock HttpServletRequest requestMock;

    RiskAssessmentService.RiskEvaluator sut;


    @BeforeMethod
    protected void setup() {
        MockitoAnnotations.initMocks(this);
        when(requestMock.getRemoteAddr()).thenReturn("127.0.0.1");
        sut = new DailyApplicationLimitRiskEvaluator(activityRepositoryMock, requestMock, DAILY_APPLICATION_LIMIT);
    }
 

    @Test
    public void shouldCheckLoggedApplicationFromDb() {
        // arrange
        doReturn(1).when(activityRepositoryMock)
                .countByTypeAndIpAddressAndEventTimeAfter(any(Activity.ActivityType.class), any(String.class), any(DateTime.class));

        // act
        sut.evaluate(VALID_LOAN_APPLICATION);

        // assert
        verify(activityRepositoryMock, times(1))
                .countByTypeAndIpAddressAndEventTimeAfter(any(Activity.ActivityType.class), any(String.class), any(DateTime.class));
    }

    @Test
    public void shouldPassLastApplicationMatchingApplicationLimit() {
        // arrange
        doReturn(DAILY_APPLICATION_LIMIT)
                .when(activityRepositoryMock)
                .countByTypeAndIpAddressAndEventTimeAfter(any(Activity.ActivityType.class), any(String.class), any(DateTime.class));

        // act
        sut.evaluate(VALID_LOAN_APPLICATION);

        // assert
        // no error == it pass
    }

    @Test(expectedExceptions = RiskyLoanApplicationException.class)
    public void shouldThrowExceptionOnExceededApplicationLimit() {
        // arrange
        doReturn(DAILY_APPLICATION_LIMIT + 1)
                .when(activityRepositoryMock)
                .countByTypeAndIpAddressAndEventTimeAfter(any(Activity.ActivityType.class), any(String.class), any(DateTime.class));

        // act
        sut.evaluate(VALID_LOAN_APPLICATION);
    }

    @Test
    public void shouldDescribeRejectionReason() {
        // arrange
        doReturn(DAILY_APPLICATION_LIMIT + 1)
                .when(activityRepositoryMock)
                .countByTypeAndIpAddressAndEventTimeAfter(any(Activity.ActivityType.class), any(String.class), any(DateTime.class));
        try {
            // act
            sut.evaluate(VALID_LOAN_APPLICATION);
        } catch (RiskyLoanApplicationException ex) {
            // arrange
            Assertions.assertThat(ex.getReason()).isEqualTo("Max applications limit per day reached.");
        }
    }
}
