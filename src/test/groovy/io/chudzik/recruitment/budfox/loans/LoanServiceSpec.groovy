package io.chudzik.recruitment.budfox.loans

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.clients.ClientService
import io.chudzik.recruitment.budfox.loans.dto.LoanNotFoundException

import spock.lang.Subject

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.basicConditions
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.client
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.conditionsAfterFirstExtension
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.invalidId
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loan
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanApplication
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId

class LoanServiceSpec extends BaseUnitSpec {

    LoanRepository loanRepoMock = Mock()
    ClientService clientServiceMock = Mock()
    LoanConditionsService conditionsServiceMock = Mock()

    @Subject def sut = new LoanService(clientServiceMock, loanRepoMock, conditionsServiceMock)


    def "should operate on client entity fetched from db"() {
        given:
            conditionsServiceMock.calculateInitialLoanConditions(_ as LoanApplication) >> basicConditions()
        when:
            sut.issueALoan(loanApplication())
        then:
            1 * clientServiceMock.getOne(loanApplication().getClientId()) >> client()
    }


    def "should persist issued loan to db"() {
        given:
            conditionsServiceMock.calculateInitialLoanConditions(_ as LoanApplication) >> basicConditions()
        and:
            clientServiceMock.getOne(validId()) >> client()
        when:
            sut.issueALoan(loanApplication())
        then:
            1 * loanRepoMock.save(_ as Loan)    // FIXME-ach: replace with verification of arguments' values versus values from unsavedLoan()
    }


    def "should persist extended loans to db"() {
        given:
            final Loan loan = loan()
        and:
            conditionsServiceMock.loanExtensionConditions(loan) >> conditionsAfterFirstExtension()
        when:
            sut.extendLoan(loan.id)
        then:
            1 * loanRepoMock.findById(loan.id) >> Optional.of(loan)
            1 * loanRepoMock.save(_ as Loan)    // FIXME-ach: replace with verification of arguments' values versus values from loanAfterFirstExtension()
    }


    def "should throw exception on extended loans to db"() {
        when:
            sut.extendLoan(invalidId())
        then:
            1 * loanRepoMock.findById(invalidId()) >> Optional.empty()
        and:
            LoanNotFoundException ex = thrown()
            ex.message == "Loan with given ID not found."
    }

}
