package io.chudzik.recruitment.budfox.repository

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.model.Client
import io.chudzik.recruitment.budfox.model.Loan

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.web.ServletTestExecutionListener
import org.springframework.transaction.annotation.Transactional
import org.testng.annotations.Ignore
import spock.lang.Subject

import javax.persistence.EntityManager

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.CLIENT_WITH_LOANS

@Transactional
@DatabaseSetup("clientWithLoansData.xml")
@TestExecutionListeners([
    ServletTestExecutionListener,
    DependencyInjectionTestExecutionListener,
    DirtiesContextTestExecutionListener,
    TransactionDbUnitTestExecutionListener
])
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
class ClientRepositoryITSpec extends BaseClockFixedITSpec {

    @Autowired EntityManager em
    @Autowired
    @Subject ClientRepository sut


    @Ignore("was ignored")
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
