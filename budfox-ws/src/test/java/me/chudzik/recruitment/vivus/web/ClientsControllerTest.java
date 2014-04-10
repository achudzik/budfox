package me.chudzik.recruitment.vivus.web;

import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.CLIENT_WITH_LOANS;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_ID;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_PESEL;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import me.chudzik.recruitment.vivus.configuration.ControllerTestConfiguration;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ClientRepository;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes = ControllerTestConfiguration.class)
public class ClientsControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private MockMvc mockMvc;

    private ClientsController sut;

    @Mock
    private ClientRepository clientRepositoryMock;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new ClientsController(clientRepositoryMock);
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setHandlerExceptionResolvers(exceptionResolver)
                .setMessageConverters(messageConverter)
                .build();
    }

    @Test
    public void shouldSaveNewEntityToDb() throws Exception {
        // arrange
        Client client = Client.builder().identificationNumber(VALID_PESEL).build();

        // act
        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(MediaType.APPLICATION_JSON));
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

        // assert
        verify(clientRepositoryMock).save(client);
    }

    @Test
    public void shouldNotSaveAlreadyPersistedEntity() throws Exception {
        // arrange
        Client client = Client.builder().id(VALID_ID).identificationNumber(VALID_PESEL).build();

        // act / assert
        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(MediaType.APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(clientRepositoryMock);
    }

    @Test
    public void shouldAllowListingClientsLoans() throws Exception {
        // arrange
        Long clientId = CLIENT_WITH_LOANS.getId(); 
        doReturn(CLIENT_WITH_LOANS).when(clientRepositoryMock).getClientLoans(clientId);

        // act
        mockMvc.perform(get("/clients/{id}/loans", clientId))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.").isArray());

        verify(clientRepositoryMock, times(1)).getClientLoans(clientId);
        verifyNoMoreInteractions(clientRepositoryMock);
    }

    @Test
    public void shouldThrowExceptionOnFetchingLoansOfNonExistingClient() throws Exception {
        // arrange
        Long clientId = CLIENT_WITH_LOANS.getId();
        doReturn(null).when(clientRepositoryMock).getClientLoans(clientId);

        // act
        mockMvc.perform(get("/clients/{id}/loans", clientId))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Client with given ID not found."))
                .andExpect(jsonPath("details").value(String.format("Client ID: %d", clientId)));
    }

}
