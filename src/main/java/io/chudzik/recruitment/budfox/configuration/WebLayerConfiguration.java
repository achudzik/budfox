package io.chudzik.recruitment.budfox.configuration;

import io.chudzik.recruitment.budfox.configuration.WebLayerConfiguration.JsonMappingConfiguration;
import io.chudzik.recruitment.budfox.support.json.MoneyModule;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Import({ GlobalExceptionHandler.class, JsonMappingConfiguration.class })
//@EnableWebMvc
@Configuration
public class WebLayerConfiguration {

    @Configuration
    public static class JsonMappingConfiguration {

        @Bean
        Module moneyModule() {
            //TODO-ach: migrate to JodaMoneyModule (out of the box implementation)
            return new MoneyModule();
        }

        @Bean
        Module jodaTimeModule() {
            return new JodaModule();
        }


        @VisibleForTesting
        public static ObjectMapper objectMapper() {
            Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
            JsonMappingConfiguration config = new JsonMappingConfiguration();
            builder.modulesToInstall(config.moneyModule(), config.jodaTimeModule());
            return builder.build();
        }

    }

}
