package io.chudzik.recruitment.budfox.configuration;

import io.chudzik.recruitment.budfox.web.GlobalExceptionHanlder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

@Import(JsonMappingConfiguration.class)
@TestConfiguration
public class ControllerTestConfiguration {

    @Autowired JsonMappingConfiguration jsonMappingConfiguration;


    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        return new MappingJackson2HttpMessageConverter(jsonMappingConfiguration.jacksonObjectMapper());
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
