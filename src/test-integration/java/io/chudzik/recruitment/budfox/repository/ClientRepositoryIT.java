package io.chudzik.recruitment.budfox.repository;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.model.Loan;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.CLIENT_WITH_LOANS;

@ActiveProfiles(TEST_INTEGRATION)
@SpringBootTest(classes = BudfoxApplication.class)
@TestExecutionListeners({
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@Transactional
@DatabaseSetup("clientWithLoansData.xml")
public class ClientRepositoryIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ClientRepository sut;

    @Autowired
    private EntityManager em;


    @Test(enabled = false)
    public void shouldFetchClientWithAllHisLoansAtOnce() {
        // act
        Client client = sut.getClientLoans(CLIENT_WITH_LOANS.getId());
        em.clear();

        // assert
        Set<Loan> loans = client.getLoans();
        assertThat(client).isNotNull();
        assertThat(loans)
                .isNotEmpty()
                .containsOnly(loans.toArray(new Loan[loans.size()]));
    }

    @Test
    public void shouldFindClientByHisLoan() {
        // act
        Client existingClient = sut.getClientLoans(CLIENT_WITH_LOANS.getId());
        Client nonExistingClient = sut.getClientLoans(-1L);

        // assert
        assertThat(existingClient).isNotNull();
        assertThat(nonExistingClient).isNull();
    }

}
