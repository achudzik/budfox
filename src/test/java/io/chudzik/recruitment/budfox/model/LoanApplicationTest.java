package io.chudzik.recruitment.budfox.model;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import io.chudzik.recruitment.budfox.configuration.JsonMappingConfiguration;
import io.chudzik.recruitment.budfox.utils.PreExistingEntities;
import io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoanApplicationTest {

    private static final ObjectMapper objectMapper = JsonMappingConfiguration.objectMapper();

    @Test
    public void shouldSerializeJustClientId() throws IOException {
        // arrange
        Client client = Client.builder().id(PreExistingEntities.VALID_ID).identificationNumber(PreExistingEntities.VALID_PESEL).build();
        LoanApplication application = LoanApplication.builder()
                .client(client).amount(PreExistingEntities.THREE_PLN).term(PreExistingEntities.THREE_WEEKS_PERIOD).build();

        // act
        String result = objectMapper.writeValueAsString(application);

        // assert
        with(result).assertThat("client", JsonPathMatchers.hasIdAs(client));
    }

    @Test
    public void shouldDeserializeClientObjectFromJustClientId() throws IOException {
        // arrange
        String json = "{\"client\":1,\"amount\":\"PLN 3.00\",\"term\":\"P3W\"}";

        // act
        LoanApplication result = objectMapper.readValue(json, LoanApplication.class);
        Client deserializedClient = result.getClient();

        // assert
        assertThat(deserializedClient.getId()).isEqualTo(PreExistingEntities.VALID_CLIENT.getId());
    }

}
