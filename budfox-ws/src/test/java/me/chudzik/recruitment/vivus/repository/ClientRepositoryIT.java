package me.chudzik.recruitment.vivus.repository;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.BASIC_INTEREST;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.CLIENT_WITH_LOANS;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.ELEVEN_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.MONTH_AND_A_TWO_WEEKS_LATER;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.MONTH_LATER;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Set;

import javax.persistence.EntityManager;

import me.chudzik.recruitment.vivus.Application;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanConditions;
import me.chudzik.recruitment.vivus.utils.DbUnitDumper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@SpringApplicationConfiguration(classes = Application.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class, DbUnitTestExecutionListener.class })
@Transactional
public class ClientRepositoryIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ClientRepository sut;
@Autowired
private LoanRepository loanRepository;
    @Autowired
    private EntityManager em;

    @Autowired
    DbUnitDumper dumper;
    @Test
    public void test1() {
        Client client = sut.save(VALID_CLIENT);

        // issue original loan
        LoanConditions conditions = LoanConditions.builder()
                .amount(THREE_PLN)
                .maturityDate(MONTH_LATER)
                .interest(BASIC_INTEREST)
                .build();
        Loan loan = Loan.builder()
                .conditions(conditions)
                .build();
        client.addLoan(loan);
        loan = loanRepository.save(loan);
        // issue original loan
        LoanConditions conditions2 = LoanConditions.builder()
                .amount(ELEVEN_PLN)
                .maturityDate(MONTH_AND_A_TWO_WEEKS_LATER)
                .interest(BASIC_INTEREST)
                .build();
        Loan loan2 = Loan.builder()
                .conditions(conditions2)
                .build();
        client.addLoan(loan2);
        loan = loanRepository.save(loan2);

        dumper.printDbFullDataSet();
    }

    @Test(dependsOnMethods = "test1")
    @DatabaseSetup("clientWithLoansData.xml")
    public void test2() {}

    @Test(enabled = false)
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

    @Test(enabled = false)
    public void shouldFindClientByHisLoan() {
        // act
        Client client = sut.findByLoansId(1L);
    
        // assert
        assertThat(client).isNotNull();
    }

}
