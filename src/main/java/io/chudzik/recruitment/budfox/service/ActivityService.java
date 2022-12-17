package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.model.Activity;
import io.chudzik.recruitment.budfox.model.Activity.ActivityType;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.repository.ActivityRepository;
import io.chudzik.recruitment.budfox.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_APPLICATION;
import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_EXTENSION;

@Transactional
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ClientRepository clientRepository;


    public void logLoanApplication(Long clientId, HttpServletRequest request) {
        // FIXME-ach: Client client = clientRepository.getReferenceById(clientId);
        Client client = clientRepository.getOne(clientId);
        saveActivity(LOAN_APPLICATION, client, request);
    }


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
