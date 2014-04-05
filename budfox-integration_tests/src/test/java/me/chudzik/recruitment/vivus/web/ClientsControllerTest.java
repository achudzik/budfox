package me.chudzik.recruitment.vivus.web;

import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ClientRepository;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ClientsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ClientsController sut;

    @Mock
    private ClientRepository repository;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void shouldSaveNewEntityToDb() throws Exception {
        // arrange
        Client client = Client.builder().identificationNumber("68092005286").build();

        // act
        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(MediaType.APPLICATION_JSON));

        // assert
        ArgumentCaptor<Client> argument = ArgumentCaptor.forClass(Client.class);
        verify(repository).save(argument.capture());
        assertThat(argument).isEqualsToByComparingFields(argument);
    }

    @Test
    public void shouldNotSaveAlreadyPersistedEntityByPut() throws Exception {
        // arrange
        Client client = Client.builder().id(1l).identificationNumber("68092005286").build();

        // act / assert
        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(MediaType.APPLICATION_JSON))
                // TODO-ach: split into two tests? First check how many methods will throw IllegalStateExceptions 
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any(Client.class));
    }
}
