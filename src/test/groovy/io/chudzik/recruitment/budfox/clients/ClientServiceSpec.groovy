package io.chudzik.recruitment.budfox.clients

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.clients.dto.ClientException.ClientNotFoundException

import spock.lang.Subject

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId

class ClientServiceSpec extends BaseUnitSpec {

    ClientRepository clientRepoMock = Mock()

    @Subject def sut = new ClientService(clientRepoMock)


    def "should throw exception on non existing client"() {
        given:
            final Long idOfNonExistingClient = 1844L
            clientRepoMock.getOne(idOfNonExistingClient) >> null
        when:
            sut.validateClientExistence(idOfNonExistingClient)
        then:
            ClientNotFoundException ex = thrown()
            ex.message == "Client with given ID not found."
    }


    def "should do nothing on existing client"() {
        given:
            clientRepoMock.getOne(_) >> VALID_CLIENT
        when:
            sut.validateClientExistence(validId())
        then:
            noExceptionThrown()
    }

}
