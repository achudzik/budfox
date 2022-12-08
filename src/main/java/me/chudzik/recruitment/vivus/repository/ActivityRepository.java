package me.chudzik.recruitment.vivus.repository;

import me.chudzik.recruitment.vivus.model.Activity;
import me.chudzik.recruitment.vivus.model.Activity.ActivityType;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // TODO-ach: replace with countLoanApplicationsBy...
    int countByTypeAndIpAddressAndEventTimeAfter(ActivityType type, String ipAddress, DateTime eventTime);

}
