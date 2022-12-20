package io.chudzik.recruitment.budfox.activities;

import io.chudzik.recruitment.budfox.activities.dto.LogActivityRequest;
import io.chudzik.recruitment.budfox.clients.Client;

import org.joda.time.DateTime;

class ActivityCreator {

    public static Activity from(LogActivityRequest request) {
        return new Activity(
                Client.builder().id(0L).build(),
                ActivityType.LOAN_APPLICATION,
                request.getMethodSignature(),
                "127.0.0.1",
                DateTime.now()
        );
    }

}
