package io.chudzik.recruitment.budfox.activities;

import io.chudzik.recruitment.budfox.clients.Client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Objects;

@JsonIgnoreProperties("new")
@Table(name = "activities")
@Entity
@ToString
@Getter
@EqualsAndHashCode
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class Activity extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -115892659003310229L;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    private String methodCall;
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    @Column(name = "event_time", nullable = false)
    private DateTime eventTime;


    @PrePersist
    public void prePersist() {
        eventTime = DateTime.now();
    }


    @Override
    protected void setId(Long id) {
        if (!Objects.equals(this.getId(), id)) {
            super.setId(id);
            log.trace("Id set for Activity [activity={}]", this);
        }
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
