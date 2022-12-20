package io.chudzik.recruitment.budfox.activities;

import io.chudzik.recruitment.budfox.clients.ClientService;

import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ConditionalOnProperty(prefix = "budfox.audit", name = "enabled")
@EnableAspectJAutoProxy
@Configuration
@RequiredArgsConstructor
public class ActivitiesConfig {

    private final ClientService clientService;


    @Bean ActivityService activityService(ActivityRepository activityRepository) {
        return new ActivityService(activityRepository, clientService);
    }

    @Bean ActivitiesFacade activitiesFacade(ActivityService activityService) {
        return new ActivitiesFacade(activityService);
    }

    @Bean LogActivityAspect logActivityAspect(ActivitiesFacade activitiesFacade) {
        return new LogActivityAspect(activitiesFacade);
    }


    @VisibleForTesting static ActivityRepository inMemoryActivityRepository() {
        return new InMemoryActivityRepository();
    }

}
