package me.chudzik.recruitment.vivus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "activities")
public class Activity extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -115892659003310229L;

    public enum ActivityType {

        LOAN_APPLICATION,
        LOAN_EXTENSION;

    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    @Column(name = "event_time", nullable = false)
    private DateTime eventTime;


    public Activity() { }


    public Client getClient() {
        return client;
    }

    public ActivityType getType() {
        return type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public DateTime getEventTime() {
        return eventTime;
    }


    @PrePersist
    public void prePersist() {
        eventTime = DateTime.now();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Client client;
        private ActivityType type;
        private String ipAddress;

        private Builder() { }

        public Builder client(Client client) {
            this.client = client;
            return this;
        }

        public Builder type(ActivityType type) {
            this.type = type;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Activity build() {
            Activity activity = new Activity();
            activity.client = this.client;
            activity.ipAddress = this.ipAddress;
            activity.type = this.type;
            return activity;
        }

    }

}
