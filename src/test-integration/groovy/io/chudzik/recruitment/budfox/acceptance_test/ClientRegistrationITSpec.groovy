package io.chudzik.recruitment.budfox.acceptance_test

import io.chudzik.recruitment.budfox.BaseITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.model.Client

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.annotation.ExpectedDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.web.context.WebApplicationContext

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT
import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
class ClientRegistrationITSpec extends BaseITSpec {

    @Autowired ObjectMapper objectMapper
    @Autowired MockMvc mockMvc
    @Autowired Environment environment
    @Autowired WebApplicationContext webApplicationContext


    @ExpectedDatabase(value = "clientData-add-expected.xml", assertionMode = NON_STRICT)
    def "should allow registering new user"() {
        given:
            Client client = Client.builder().identificationNumber("68092005286").build()
        when:
            ResultActions result = mockMvc.perform(
                post("/clients")
                    .content(objectMapper.writeValueAsBytes(client))
                    .contentType(APPLICATION_JSON)
            )
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        then:
            result
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("identificationNumber").value(client.getIdentificationNumber()))
    }

}
