package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.repository.ClientRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.CLIENT_WITH_LOANS;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_PESEL;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId;

@ActiveProfiles(TEST_INTEGRATION)
@TestExecutionListeners(MockitoTestExecutionListener.class)
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication.class, webEnvironment = MOCK)
//@WebMvcTest(controllers = ClientsController.class)
public class ClientsControllerIT extends AbstractTestNGSpringContextTests {

    @MockBean ClientRepository clientRepositoryMock;

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @Autowired ClientsController sut;


    @BeforeMethod
    public void setup() {
        Mockito.reset(clientRepositoryMock);
    }


    @Test
    public void shouldSaveNewEntityToDb() throws Exception {
        // arrange
        Client client = Client.builder().identificationNumber(VALID_PESEL).build();

        // act
        ResultActions result = mockMvc.perform(
                post("/clients")
                    .content(objectMapper.writeValueAsBytes(client))
                    .contentType(APPLICATION_JSON)
                )
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;

        // assert
        verify(clientRepositoryMock).save(client);
    }


    @Test
    public void shouldNotOverwriteAlreadyExistingEntity() throws Exception {
        // arrange
        Client client = Client.builder()
                .id(validId())
                .identificationNumber(VALID_PESEL)
                .build();

        // act
        ResultActions result = mockMvc.perform(
                post("/clients")
                    .content(objectMapper.writeValueAsBytes(client))
                    .contentType(APPLICATION_JSON)
                )
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;

        // assert
        result.andExpect(status().isBadRequest());
    }


    @Test
    public void shouldAllowListingClientsLoans() throws Exception {
        // arrange
        final Long clientId = CLIENT_WITH_LOANS.getId();
        doReturn(CLIENT_WITH_LOANS).when(clientRepositoryMock).getClientLoans(clientId);

        // act
        ResultActions result = mockMvc.perform(
                get("/clients/{id}/loans", clientId)
                        .contentType(APPLICATION_JSON)
                )
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    public void shouldThrowExceptionOnFetchingLoansOfNonExistingClient() throws Exception {
        // arrange
        final Long clientId = CLIENT_WITH_LOANS.getId();
        doReturn(null).when(clientRepositoryMock).getClientLoans(clientId);

        // act
        ResultActions result = mockMvc.perform(
                get("/clients/{id}/loans", clientId)
                        .contentType(APPLICATION_JSON)
                )
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Client with given ID not found."))
        ;
    }

}
