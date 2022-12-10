package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.service.impl.RiskAssessmentServiceImpl;
import io.chudzik.recruitment.budfox.service.risk_evaluator.BaseRiskEvaluator;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION;


public class RiskAssessmentServiceTest {

    private RiskAssessmentService sut;

    @Spy
    private RiskAssessmentService.RiskEvaluator evaluator1 = new DoNothingRiskEvaluator();
    @Spy
    private RiskAssessmentService.RiskEvaluator evaluator2 = new DoNothingRiskEvaluator();
    @Spy
    private RiskAssessmentService.RiskEvaluator evaluator3 = new DoNothingRiskEvaluator();

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new RiskAssessmentServiceImpl(List.of(evaluator1, evaluator2, evaluator3));
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
