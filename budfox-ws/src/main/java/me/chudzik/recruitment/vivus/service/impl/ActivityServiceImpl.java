package me.chudzik.recruitment.vivus.service.impl;

import static me.chudzik.recruitment.vivus.model.Activity.ActivityType.LOAN_APPLICATION;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.model.Activity;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ActivityRepository;
import me.chudzik.recruitment.vivus.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;

public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;
    
    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void logLoanApplication(Long clientId, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Client client = Client.builder().id(clientId).build();
        Activity activity = Activity.builder()
                .client(client)
                .ipAddress(ipAddress)
                .type(LOAN_APPLICATION)
                .build();

        activityRepository.save(activity);
    }

    
}
