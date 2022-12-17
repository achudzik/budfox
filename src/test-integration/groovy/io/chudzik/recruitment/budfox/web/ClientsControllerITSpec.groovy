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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
            mockMvc.perform(
                        post("/clients")
                            .content(objectMapper.writeValueAsBytes(client))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn()
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
            ResultActions result = mockMvc.perform(
                get("/clients/{id}/loans", clientId)
                    .contentType(APPLICATION_JSON)
            )
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        then:
            result
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$.length()').value(2))
    }


    def "should throw exception on fetching loans of non existing client"() {
        given:
            final Long clientId = CLIENT_WITH_LOANS.getId()
            clientRepositoryMock.getClientLoans(clientId) >> null
        when:
            ResultActions result = mockMvc.perform(
                get("/clients/{id}/loans", clientId)
                    .contentType(APPLICATION_JSON)
            )
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        then:
            result
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Client with given ID not found."))

    }

}
