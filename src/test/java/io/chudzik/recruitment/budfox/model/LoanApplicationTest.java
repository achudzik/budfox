package io.chudzik.recruitment.budfox.model;

import io.chudzik.recruitment.budfox.configuration.WebLayerConfiguration.JsonMappingConfiguration;
import io.chudzik.recruitment.budfox.utils.PreExistingEntities;
import io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.fest.assertions.api.Assertions.assertThat;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_PESEL;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId;

public class LoanApplicationTest {

    private static final ObjectMapper objectMapper = JsonMappingConfiguration.objectMapper();


    @Test
    public void shouldSerializeJustClientId() throws IOException {
        // arrange
        Client client = Client.builder()
                .id(validId())
                .identificationNumber(VALID_PESEL)
                .build();
        LoanApplication application = LoanApplication.builder()
                .client(client)
                .amount(THREE_PLN)
                .term(THREE_WEEKS_PERIOD)
                .build();

        // act
        String result = objectMapper.writeValueAsString(application);

        // assert
        with(result).assertThat("client", JsonPathMatchers.hasIdAs(client));
    }


    @Test
    public void shouldDeserializeClientObjectFromJustClientId() throws IOException {
        // arrange
        String json = "{\"client\":0,\"amount\":\"PLN 3.00\",\"term\":\"P3W\"}";

        // act
        LoanApplication result = objectMapper.readValue(json, LoanApplication.class);
        Client deserializedClient = result.getClient();

        // assert
        assertThat(deserializedClient.getId()).isEqualTo(PreExistingEntities.VALID_CLIENT.getId());
    }

}
