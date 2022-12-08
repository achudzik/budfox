package io.chudzik.recruitment.budfox.service.risk_evaluator;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;

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

public class DailyApplicationLimitRiskEvaluatorTest {

    private RiskAssessmentService.RiskEvaluator sut;

    private final Integer dailyApplicationLimit = 2;
    @Mock
    private ActivityRepository activityRepositoryMock;
    @Mock
    private HttpServletRequest requestMock;

    @BeforeMethod
    protected void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new DailyApplicationLimitRiskEvaluator(activityRepositoryMock, requestMock, dailyApplicationLimit);
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
        doReturn(dailyApplicationLimit)
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
        doReturn(dailyApplicationLimit + 1)
                .when(activityRepositoryMock)
                .countByTypeAndIpAddressAndEventTimeAfter(any(Activity.ActivityType.class), any(String.class), any(DateTime.class));

        // act
        sut.evaluate(VALID_LOAN_APPLICATION);
    }

    @Test
    public void shouldDescribeRejectionReason() {
        // arrange
        doReturn(dailyApplicationLimit + 1)
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
