package io.chudzik.recruitment.budfox.configuration;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chudzik.recruitment.budfox.web.GlobalExceptionHanlder;

import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Configuration
@Import({ HttpMessageConvertersAutoConfiguration.class, JsonMappingConfiguration.class })
public class ControllerTestConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Bean
    public GlobalExceptionHanlder globalExceptionHanlder() {
        return new GlobalExceptionHanlder();
    }

    // XXX-ach: post as better sollution to https://stackoverflow.com/questions/15302243 ; current top one cuts controller's errorHandlers
    @Bean
    public ExceptionHandlerExceptionResolver exceptionResolver(MappingJackson2HttpMessageConverter messageConverter) {
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(messageConverter);
        exceptionHandlerExceptionResolver.setMessageConverters(messageConverters);
        exceptionHandlerExceptionResolver.afterPropertiesSet();
        return exceptionHandlerExceptionResolver;
    }

}
