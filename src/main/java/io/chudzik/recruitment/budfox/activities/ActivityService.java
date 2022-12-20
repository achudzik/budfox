package io.chudzik.recruitment.budfox.activities;

import io.chudzik.recruitment.budfox.activities.dto.LogActivityRequest;
import io.chudzik.recruitment.budfox.clients.Client;
import io.chudzik.recruitment.budfox.clients.ClientService;
import io.chudzik.recruitment.budfox.loans.LoanApplication;

import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static io.chudzik.recruitment.budfox.activities.ActivityType.LOAN_APPLICATION;
import static io.chudzik.recruitment.budfox.activities.ActivityType.LOAN_EXTENSION;

@Transactional
@RequiredArgsConstructor
class ActivityService {

    private final ActivityRepository activityRepository;
    private final ClientService clientService;


    public void logLoanApplication(Long clientId, HttpServletRequest request) {
        // FIXME-ach: Client client = clientRepository.getReferenceById(clientId);
        Client client = clientService.getOne(clientId);
        saveActivity(LOAN_APPLICATION, client, request);
    }


    public void logLoanExtension(Long loanId, HttpServletRequest request) {
        Client client = clientService.getReferenceId(loanId);
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

    public long countLoanApplicationsByIpAddressAndEventTimeAfter(LoanApplication application, String ipAddress) {
        // XXX-ach: length of given period can also be an injectable parameter
        DateTime twentyFourHoursEarlier = application.getApplicationDate().minusDays(1);
        return activityRepository
                .countByTypeAndIpAddressAndEventTimeAfter(LOAN_APPLICATION, ipAddress, twentyFourHoursEarlier);
    }

    public void logActivity(LogActivityRequest request) {
        Activity newActivity = ActivityCreator.from(request);
        activityRepository.save(newActivity);
    }

    public long count() {
        return activityRepository.count();
    }

}
