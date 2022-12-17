package io.chudzik.recruitment.budfox.commons.web.debug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Slf4j
class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

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
