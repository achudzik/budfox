package io.chudzik.recruitment.budfox.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;

@Profile(TEST_INTEGRATION)
@Import(WebLayerConfiguration.class)
@TestConfiguration
public class ControllerTestConfiguration {

    @Bean
    MappingJackson2HttpMessageConverter messageConverter(ObjectMapper budFoxObjectMapper) {
        return new MappingJackson2HttpMessageConverter(budFoxObjectMapper);
    }


    // XXX-ach: post as better sollution to https://stackoverflow.com/questions/15302243 ; current top one cuts controller's errorHandlers
    @Bean
    ExceptionHandlerExceptionResolver exceptionResolver(MappingJackson2HttpMessageConverter messageConverter) {
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(messageConverter);
        exceptionHandlerExceptionResolver.setMessageConverters(messageConverters);
        exceptionHandlerExceptionResolver.afterPropertiesSet();
        return exceptionHandlerExceptionResolver;
    }

}
