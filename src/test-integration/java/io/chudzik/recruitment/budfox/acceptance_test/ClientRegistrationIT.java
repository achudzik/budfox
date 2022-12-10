package io.chudzik.recruitment.budfox.acceptance_test;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static io.chudzik.recruitment.budfox.utils.JsonUtils.convertObjectToJsonBytes;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.Client;

import io.chudzik.recruitment.budfox.utils.AdjustableTimeProviderSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@WebAppConfiguration
@SpringApplicationConfiguration(classes = BudfoxApplication.class)
@TestExecutionListeners({
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@Transactional
@DatabaseSetup("clientData.xml")
public class ClientRegistrationIT extends AbstractTestNGSpringContextTests {

    @Autowired WebApplicationContext webApplicationContext;

    MockMvc mockMvc;


    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @ExpectedDatabase(value = "clientData-add-expected.xml", assertionMode = NON_STRICT)
    public void shouldAllowRegisteringNewUser() throws Exception {
        Client client = Client.builder().identificationNumber("68092005286").build();

        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("id").value(NOT_NULL))
                .andExpect(jsonPath("identificationNumber").value(client.getIdentificationNumber()));
    }

}
