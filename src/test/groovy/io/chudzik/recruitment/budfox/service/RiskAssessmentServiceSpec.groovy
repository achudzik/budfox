package io.chudzik.recruitment.budfox.service

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException
import io.chudzik.recruitment.budfox.model.LoanApplication
import io.chudzik.recruitment.budfox.service.impl.RiskAssessmentServiceImpl
import io.chudzik.recruitment.budfox.service.risk_evaluator.BaseRiskEvaluator
import spock.lang.Subject

import java.util.concurrent.atomic.AtomicInteger

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION

@Subject([ RiskAssessmentService, RiskAssessmentServiceImpl ])
class RiskAssessmentServiceSpec extends BaseUnitSpec {

    def "shouldGoThroughAllEvaluatorsIfNoExceptions"() {
        given:
            List<CountingNoOpRiskEvaluator> evaluators = [
                    new CountingNoOpRiskEvaluator(),
                    new CountingNoOpRiskEvaluator(),
                    new CountingNoOpRiskEvaluator()
            ]
            def sut = new RiskAssessmentServiceImpl(evaluators)
        when:
            sut.validateApplicationSafety(VALID_LOAN_APPLICATION)
        then:
            evaluators*.evaluationCount*.intValue() == [1, 1, 1]
    }


    def "should stop evaluation on first exceptions"() {
        given:
            List<CountingNoOpRiskEvaluator> evaluators = [
                    new CountingNoOpRiskEvaluator(),
                    new CountingExceptionThrowingRiskEvaluator(),
                    new CountingNoOpRiskEvaluator()
            ]
            def sut = new RiskAssessmentServiceImpl(evaluators)
        when:
            sut.validateApplicationSafety(VALID_LOAN_APPLICATION)
        then:
            thrown(RiskyLoanApplicationException)
        and:
            evaluators*.evaluationCount*.intValue() == [1, 1, 0]
    }


    private class CountingNoOpRiskEvaluator extends BaseRiskEvaluator {

        AtomicInteger evaluationCount = new AtomicInteger(0)


        @Override
        protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException {
            evaluationCount.incrementAndGet()
        }

    }


    private class CountingExceptionThrowingRiskEvaluator extends CountingNoOpRiskEvaluator {

        @Override
        protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException {
            super.doEvaluation(application)
            throw new RiskyLoanApplicationException("Mocked message for risky business!")
        }

    }

}
