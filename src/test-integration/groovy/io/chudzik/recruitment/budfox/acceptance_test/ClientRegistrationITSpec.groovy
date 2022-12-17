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
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

import groovy.json.JsonSlurper

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT
import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

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
            MockHttpServletResponse response = mockMvc.perform(
                        post("/clients")
                            .content(objectMapper.writeValueAsBytes(client))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            response.status == CREATED.value()
            response.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(extendLoanResponse.contentAsString) as Client) {
                it.id != null
                it.identificationNumber == client.identificationNumber
            }
    }

}
