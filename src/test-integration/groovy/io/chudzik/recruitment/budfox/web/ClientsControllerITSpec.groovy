package io.chudzik.recruitment.budfox.web

import io.chudzik.recruitment.budfox.BaseITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.model.Client
import io.chudzik.recruitment.budfox.repository.ClientRepository

import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import groovy.json.JsonSlurper

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.CLIENT_WITH_LOANS
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_PESEL
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId

@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
// XXX-ach: redo as @WebMvcTest(controllers = ClientsController)
class ClientsControllerITSpec extends BaseITSpec {

    @SpringBean ClientRepository clientRepositoryMock = Mock()

    @Autowired ObjectMapper objectMapper
    @Autowired MockMvc mockMvc


    def "should save new entity to db"() {
        given:
            Client client = Client.builder().identificationNumber(VALID_PESEL).build()
        when:
            MockHttpServletResponse createdClientResponse = mockMvc.perform(
                        post("/clients")
                            .content(objectMapper.writeValueAsBytes(client))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            1 * clientRepositoryMock.save(_ as Client)
    }


    def "should not overwrite already existing entity"() {
        given:
            Client client = Client.builder()
                .id(validId())
                .identificationNumber(VALID_PESEL)
                .build()
        when:
            MvcResult result = mockMvc.perform(
                        post("/clients")
                            .content(objectMapper.writeValueAsBytes(client))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn()
        then:
            result.response.status == BAD_REQUEST.value()
    }


    def "should allow listing clients loans"() {
        given:
            final Long clientId = CLIENT_WITH_LOANS.getId()
            clientRepositoryMock.getClientLoans(clientId) >> CLIENT_WITH_LOANS
        when:
            MockHttpServletResponse clientLoansResponse = mockMvc.perform(
                        get("/clients/{id}/loans", clientId)
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            clientLoansResponse.status == OK.value()
            clientLoansResponse.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(clientLoansResponse.contentAsString)) {
                it instanceof List
                it[0].id != null
                it[0].id != id[1].id
                it[1].id != null
                it[0].client == it[1].client == clientId
            }
    }


    def "should throw exception on fetching loans of non existing client"() {
        given:
            final Long clientId = CLIENT_WITH_LOANS.getId()
            clientRepositoryMock.getClientLoans(clientId) >> null
        when:
            MockHttpServletResponse loansOfNotExistingClientResponse = mockMvc.perform(
                            get("/clients/{id}/loans", clientId)
                                .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            loansOfNotExistingClientResponse.status == NOT_FOUND.value()
            loansOfNotExistingClientResponse.contentType == APPLICATION_JSON_VALUE
            loansOfNotExistingClientResponse.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(loansOfNotExistingClientResponse.contentAsString)) {
                it.code == NOT_FOUND.value()
                it.message == 'Client with given ID not found.'
            }
    }

}
