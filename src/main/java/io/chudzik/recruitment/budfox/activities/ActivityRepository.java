package io.chudzik.recruitment.budfox.activities;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;

interface ActivityRepository extends JpaRepository<Activity, Long> {

    // TODO-ach: replace with countLoanApplicationsBy...
    int countByTypeAndIpAddressAndEventTimeAfter(ActivityType type, String ipAddress, DateTime eventTime);

}
