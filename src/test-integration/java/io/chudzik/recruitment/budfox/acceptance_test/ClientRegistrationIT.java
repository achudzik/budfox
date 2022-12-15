package io.chudzik.recruitment.budfox.acceptance_test;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.Test;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT;
import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;

@ActiveProfiles(TEST_INTEGRATION)
@TestExecutionListeners({
        DbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        SqlScriptsTestExecutionListener.class
})
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication.class, webEnvironment = MOCK)
public class ClientRegistrationIT extends AbstractTestNGSpringContextTests {

    @Autowired WebApplicationContext webApplicationContext;
    @Autowired ObjectMapper objectMapper;

    @Autowired MockMvc mockMvc;


    @ExpectedDatabase(value = "clientData-add-expected.xml", assertionMode = NON_STRICT)
    @Test
    public void shouldAllowRegisteringNewUser() throws Exception {
        Client client = Client.builder().identificationNumber("68092005286").build();

        ResultActions result = mockMvc.perform(
                post("/clients")
                    .content(objectMapper.writeValueAsBytes(client))
                    .contentType(APPLICATION_JSON)
                )
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());
        ;

        result
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("identificationNumber").value(client.getIdentificationNumber()));
    }

}
