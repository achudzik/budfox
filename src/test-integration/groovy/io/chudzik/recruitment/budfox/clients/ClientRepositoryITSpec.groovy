package io.chudzik.recruitment.budfox.clients

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.loans.Loan

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import spock.lang.Subject

import javax.persistence.EntityManager

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.CLIENT_WITH_LOANS

@DatabaseSetup("clientWithLoansData.xml")
@TestExecutionListeners([
        SpringBootDependencyInjectionTestExecutionListener,
        TransactionDbUnitTestExecutionListener,
])
@SpringBootTest(classes = BudfoxApplication, webEnvironment = NONE)
class ClientRepositoryITSpec extends BaseClockFixedITSpec {

    @Autowired EntityManager em
    @Autowired
    @Subject ClientRepository sut


    def "should fetch client with all his loans at once"() {
        when:
            Client client = sut.getClientLoans(CLIENT_WITH_LOANS.getId())
            em.clear()

        then:
            Set<Loan> loans = client.getLoans()
            null != client
            null != loans
//            assertThat(loans)
//                .isNotEmpty()
//                .containsOnly(loans.toArray(new Loan[loans.size()]))
    }


    def "should find client by his loan"() {
        expect:
            null != sut.getClientLoans(CLIENT_WITH_LOANS.getId())
            null == sut.getClientLoans(-1L)
    }

}
