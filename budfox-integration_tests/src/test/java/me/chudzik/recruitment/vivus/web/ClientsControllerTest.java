package me.chudzik.recruitment.vivus.web;

import static me.chudzik.recruitment.vivus.utils.Constants.APPLICATION_JSON_WITH_UTF8;
import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ClientRepository;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
        Client client = new Client.Builder().identificationNumber("68092005286").build();

        // act
        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(APPLICATION_JSON_WITH_UTF8));

        // assert
        ArgumentCaptor<Client> argument = ArgumentCaptor.forClass(Client.class);
        verify(repository).save(argument.capture());
        assertThat(argument).isEqualsToByComparingFields(argument);
    }

}
