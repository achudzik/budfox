package me.chudzik.recruitment.vivus.client;

import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import me.chudzik.recruitment.vivus.Application;
import me.chudzik.recruitment.vivus.model.Client;

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class ClientRegistrationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void shouldAllowRegisteringNewUser() throws Exception {
        Client client = new Client.Builder().identificationNumber("68092005286").build(); 

        mockMvc.perform(
                post("/clients")
                    .content(convertObjectToJsonBytes(client))
                    .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("identificationNumber").value(client.getIdentificationNumber()));
    }
}