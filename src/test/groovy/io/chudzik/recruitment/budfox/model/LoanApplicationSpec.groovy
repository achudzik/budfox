package io.chudzik.recruitment.budfox.model

import io.chudzik.recruitment.budfox.clients.Client

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.configuration.WebLayerConfiguration.JsonMappingConfiguration

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.*

class LoanApplicationSpec extends BaseUnitSpec {

    static final ObjectMapper OBJECT_MAPPER = JsonMappingConfiguration.objectMapper()


    def "should serialize only clientId from Client object"() {
        given:
            Client client = Client.builder()
                .id(validId())
                .identificationNumber(VALID_PESEL)
                .build()
            LoanApplication application = LoanApplication.builder()
                .client(client)
                .amount(THREE_PLN)
                .term(THREE_WEEKS_PERIOD)
                .build()
        when:
            String serializedResult = OBJECT_MAPPER.writeValueAsString(application)
            def result = new JsonSlurper().parseText(serializedResult)
        then:
            result.client == validId()
    }


    def "should deserialize Client object only from (and with) ID"() {
        given:
            String json = """\
                    {
                      "client": 0,
                      "amount": "PLN 3.00",
                      "term":"P3W"
                    }""".stripLeading()
        when:
            LoanApplication result = OBJECT_MAPPER.readValue(json, LoanApplication.class)
        then:
            result.clientId == 0
    }

}
