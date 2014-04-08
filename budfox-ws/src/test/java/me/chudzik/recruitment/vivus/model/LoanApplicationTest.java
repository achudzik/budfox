package me.chudzik.recruitment.vivus.model;

import static com.jayway.jsonassert.JsonAssert.with;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_PESEL;
import static me.chudzik.recruitment.vivus.utils.matchers.JsonPathMatchers.hasIdAs;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import me.chudzik.recruitment.vivus.configuration.JsonMapperConfiguration;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoanApplicationTest {

    private static final ObjectMapper objectMapper = JsonMapperConfiguration.buildObjectMapper();

    @Test
    public void shouldSerializeJustClientId() throws IOException {
        // arrange
        Client client = Client.builder().id(1l).identificationNumber(VALID_PESEL).build();
        LoanApplication application = LoanApplication.builder()
                .client(client).amount(THREE_PLN).term(THREE_WEEKS_PERIOD).build();

        // act
        String result = objectMapper.writeValueAsString(application);

        // assert
        with(result).assertEquals("$.client", hasIdAs(client));
    }

    @Test
    public void shouldDeserializeClientObjectFromJustClientId() throws IOException {
        // arrange
        String json = "{\"client\":1,\"amount\":\"PLN 3.00\",\"term\":\"P3W\"}";

        // act
        LoanApplication result = objectMapper.readValue(json, LoanApplication.class);
        Client deserializedClient = result.getClient();

        // assert
        assertThat(deserializedClient.getId()).isEqualTo(VALID_CLIENT.getId());
    }
}
