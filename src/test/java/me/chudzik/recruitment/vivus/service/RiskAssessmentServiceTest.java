package me.chudzik.recruitment.vivus.service;

import static com.google.common.collect.Lists.newArrayList;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN_APPLICATION;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.service.RiskAssessmentService.RiskEvaluator;
import me.chudzik.recruitment.vivus.service.impl.RiskAssessmentServiceImpl;
import me.chudzik.recruitment.vivus.service.risk_evaluator.BaseRiskEvaluator;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class RiskAssessmentServiceTest {

    private RiskAssessmentService sut;

    @Spy
    private RiskEvaluator evaluator1 = new DoNothingRiskEvaluator();
    @Spy
    private RiskEvaluator evaluator2 = new DoNothingRiskEvaluator();
    @Spy
    private RiskEvaluator evaluator3 = new DoNothingRiskEvaluator();

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new RiskAssessmentServiceImpl(newArrayList(evaluator1, evaluator2, evaluator3));
    }

    @Test
    public void shouldGoThroughAllEvaluatorsIfNoExceptions() {
        // act
        sut.validateApplicationSafety(VALID_LOAN_APPLICATION);

        // assert
        verify(evaluator1, times(1)).evaluate(VALID_LOAN_APPLICATION);
        verify(evaluator2, times(1)).evaluate(VALID_LOAN_APPLICATION);
        verify(evaluator3, times(1)).evaluate(VALID_LOAN_APPLICATION);
    }

    @Test(expectedExceptions = RiskyLoanApplicationException.class)
    public void shouldStopEvaluationOnFirstExceptions() {
        // arrange
        doThrow(RiskyLoanApplicationException.class).when(evaluator2).evaluate(VALID_LOAN_APPLICATION);

        // act
        sut.validateApplicationSafety(VALID_LOAN_APPLICATION);

        // assert
        verify(evaluator1, times(1)).evaluate(VALID_LOAN_APPLICATION);
        verify(evaluator2, times(1)).evaluate(VALID_LOAN_APPLICATION);
        verifyZeroInteractions(evaluator3);
    }

    private static class DoNothingRiskEvaluator extends BaseRiskEvaluator {
        @Override
        protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException { }
    }
}
