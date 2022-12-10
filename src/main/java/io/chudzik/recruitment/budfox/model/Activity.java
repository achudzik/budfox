package io.chudzik.recruitment.budfox.model;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

@Entity
@Table(name = "activities")
@JsonIgnoreProperties("new")
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


    @Override
    public int hashCode() {
        return Objects.hashCode(client, type, ipAddress, eventTime);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Activity otherActivity = (Activity) other;
        return Objects.equal(client, otherActivity.client)
                && Objects.equal(type, otherActivity.type)
                && Objects.equal(ipAddress, otherActivity.ipAddress)
                && Objects.equal(eventTime, otherActivity.eventTime);
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
