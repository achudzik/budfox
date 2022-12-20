package io.chudzik.recruitment.budfox.activities;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Profile;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static io.chudzik.recruitment.budfox.config.BudFoxProfiles.NOT_ON_PROD;

@Profile(NOT_ON_PROD)
@Slf4j
class InMemoryActivityRepository implements ActivityRepository {

    private final AtomicLong idGenerator = new AtomicLong(0L);
    private final ConcurrentHashMap<Long, Activity> map = new ConcurrentHashMap<>();


    @Override
    public Activity save(Activity activityToSave) {
        Optional.ofNullable(activityToSave)
                .map(this::generateIdIfNeeded);
        Optional.ofNullable(activityToSave)
                .map(activity -> map.put(activity.getId(), activity))
                .filter(Objects::nonNull)
                .filter(previousActivity -> Objects.equals(activityToSave.getId(), previousActivity.getId()))
                .ifPresent(activity -> log.warn("Possible data corruption in Activities [previous={}, current={}]", activity, activityToSave));
        return map.get(activityToSave.getId());
    }

    private Activity generateIdIfNeeded(Activity activityToSave) {
        if (activityToSave.isNew()) {
            Long activityId;
            do {
                activityId = idGenerator.getAndIncrement();
            } while (map.containsKey(activityId));
            activityToSave.setId(activityId);
        }
        return activityToSave;
    }


    @Override
    public long count() {
        return map.size();
    }


    @Override
    public long countByTypeAndIpAddressAndEventTimeAfter(ActivityType type, String ipAddress, DateTime eventTime) {
        return map.values().stream()
                .filter(activity -> Objects.equals(type, activity.getType()))
                .filter(activity -> Objects.equals(ipAddress, activity.getIpAddress()))
                .filter(activity -> Objects.equals(eventTime, activity.getEventTime()))
                .count();
    }

}
