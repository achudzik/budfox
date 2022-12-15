package io.chudzik.recruitment.budfox.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Listing defined request mappings with their respective handlers:");
        event.getApplicationContext()
                .getBeansOfType(RequestMappingHandlerMapping.class)
                .forEach((beanName, requestMappingHandlerMapping) -> {
                    log.info(". beanName=[{}] class=[{}]", beanName, requestMappingHandlerMapping.getClass());
                    requestMappingHandlerMapping.getHandlerMethods()
                            .forEach((requestMappingInfo, handlerMethod) -> {
                                log.info(".. [{}] -> [{}]", requestMappingInfo, handlerMethod);
                            });
                });
    }

}
