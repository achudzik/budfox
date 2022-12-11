package io.chudzik.recruitment.budfox.configuration;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.ErrorMessage;
import io.chudzik.recruitment.budfox.support.json.MoneyModule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.TimeZone;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Configuration
public class WebLayerConfiguration {

    @Configuration
    public static class JsonMappingConfiguration {

        @Bean
        ObjectMapper jacksonObjectMapper() {
            return new ObjectMapper()
                    .registerModules(
                            new MoneyModule(),
                            new JodaModule()
                    )
                    .disable(
                            SerializationFeature.FAIL_ON_EMPTY_BEANS,
                            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                    )
                    .disable(
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                    )
                    .enable(
                            DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS
                    )
                    .setTimeZone(TimeZone.getDefault());
        }

        @VisibleForTesting
        public static ObjectMapper objectMapper() {
            return new JsonMappingConfiguration().jacksonObjectMapper();
        }

    }


    @ControllerAdvice
    public static class GlobalExceptionHandler {

        private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(IllegalStateException.class)
        @ResponseStatus(BAD_REQUEST)
        void handleIllegalStateException(IllegalStateException ex) {
            LOGGER.warn(ex.getMessage());
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        @ResponseStatus(BAD_REQUEST)
        @ResponseBody
        ErrorMessage handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
            LOGGER.warn(ex.getMessage());
            return new ErrorMessage(BAD_REQUEST.value(), ex.getMessage());
        }

        @ExceptionHandler(ClientNotFoundException.class)
        @ResponseStatus(NOT_FOUND)
        @ResponseBody
        ErrorMessage handleClientNotFoundException(ClientNotFoundException ex) {
            LOGGER.warn(ex.getMessage());
            String details = String.format("Client ID: %d", ex.getClientId());
            return new ErrorMessage(NOT_FOUND.value(), ex.getMessage(), details);
        }

    }

}
