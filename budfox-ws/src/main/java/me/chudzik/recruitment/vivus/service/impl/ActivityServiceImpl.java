package me.chudzik.recruitment.vivus.service.impl;

import static me.chudzik.recruitment.vivus.model.Activity.ActivityType.LOAN_APPLICATION;
import static me.chudzik.recruitment.vivus.model.Activity.ActivityType.LOAN_EXTENSION;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.model.Activity;
import me.chudzik.recruitment.vivus.model.Activity.ActivityType;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ActivityRepository;
import me.chudzik.recruitment.vivus.repository.ClientRepository;
import me.chudzik.recruitment.vivus.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;
    private ClientRepository clientRepository;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, ClientRepository clientRepository) {
        this.activityRepository = activityRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    @Override
    public void logLoanApplication(Long clientId, HttpServletRequest request) {
        Client client = clientRepository.findOne(clientId);
        logActivity(LOAN_APPLICATION, client, request);
    }

    @Transactional
    @Override
    public void logLoanExtension(Long loanId, HttpServletRequest request) {
        Client client = clientRepository.findByLoansId(loanId);
        logActivity(LOAN_EXTENSION, client, request);
    }

    private void logActivity(ActivityType type, Client client, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Activity activity = Activity.builder()
                .client(client)
                .ipAddress(ipAddress)
                .type(type)
                .build();

        activityRepository.save(activity);
    }

}
