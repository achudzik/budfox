package io.chudzik.recruitment.budfox.service.impl;

import io.chudzik.recruitment.budfox.model.Activity;
import io.chudzik.recruitment.budfox.model.Activity.ActivityType;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.repository.ActivityRepository;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_APPLICATION;
import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_EXTENSION;

@Transactional
@Service
public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;
    private ClientRepository clientRepository;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, ClientRepository clientRepository) {
        this.activityRepository = activityRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public void logLoanApplication(Long clientId, HttpServletRequest request) {
        Client client = clientRepository.getOne(clientId);
        saveActivity(LOAN_APPLICATION, client, request);
    }

    @Override
    public void logLoanExtension(Long loanId, HttpServletRequest request) {
        Client client = clientRepository.findByLoansId(loanId);
        saveActivity(LOAN_EXTENSION, client, request);
    }

    private void saveActivity(ActivityType type, Client client, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        Activity activity = Activity.builder()
                .client(client)
                .ipAddress(ipAddress)
                .type(type)
                .build();

        activityRepository.save(activity);
    }

}
