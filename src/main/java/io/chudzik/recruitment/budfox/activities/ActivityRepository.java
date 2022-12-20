package io.chudzik.recruitment.budfox.activities;

import org.joda.time.DateTime;
import org.springframework.data.repository.Repository;

interface ActivityRepository extends Repository<Activity, Long> {

    Activity save(Activity activity);
    long count();
    // TODO-ach: replace with countLoanApplicationsBy...
    long countByTypeAndIpAddressAndEventTimeAfter(ActivityType type, String ipAddress, DateTime eventTime);

}
