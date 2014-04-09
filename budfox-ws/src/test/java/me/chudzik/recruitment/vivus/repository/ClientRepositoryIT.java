package me.chudzik.recruitment.vivus.repository;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.CLIENT_WITH_LOANS;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Set;

import javax.persistence.EntityManager;

import me.chudzik.recruitment.vivus.Application;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.model.Loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.testng.annotations.Test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@SpringApplicationConfiguration(classes = Application.class)
@TestExecutionListeners({
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
public class ClientRepositoryIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ClientRepository sut;

    @Autowired
    private EntityManager em;

    @Test
    @DatabaseSetup("clientWithLoansData.xml")
    public void shouldFetchClientWithAllHisLoansAtOnce() {
        // act
        Client client = sut.getClientLoans(CLIENT_WITH_LOANS.getId());
        Set<Loan> loans = client.getLoans(); 
        em.clear();

        // assert
        assertThat(client).isNotNull();
        assertThat(loans)
                .isNotEmpty()
                .containsOnly(loans.toArray(new Loan[loans.size()]));
    }
}
