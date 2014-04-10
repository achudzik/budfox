package me.chudzik.recruitment.vivus.configuration;

import java.util.ArrayList;
import java.util.List;

import me.chudzik.recruitment.vivus.web.GlobalExceptionHanlder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Configuration
@Import({ HttpMessageConvertersAutoConfiguration.class, JsonMapperConfiguration.class })
public class ControllerTestConfiguration {

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;

    @Bean
    public GlobalExceptionHanlder globalExceptionHanlder() {
        return new GlobalExceptionHanlder();
    }

    // XXX-ach: post as better sollution to https://stackoverflow.com/questions/15302243 ; current top one cuts controller's errorHandlers
    @Bean
    public ExceptionHandlerExceptionResolver exceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(messageConverter);
        exceptionHandlerExceptionResolver.setMessageConverters(messageConverters);
        exceptionHandlerExceptionResolver.afterPropertiesSet();
        return exceptionHandlerExceptionResolver;
    }

}
