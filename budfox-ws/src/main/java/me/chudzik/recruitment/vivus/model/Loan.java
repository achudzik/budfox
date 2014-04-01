package me.chudzik.recruitment.vivus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "loans")
public class Loan extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1240565067423749483L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    private LoanConditions conditions;
    @Column(name = "creation_time", nullable = false)
    private DateTime creationTime;

    @SuppressWarnings("unused")
    @Version
    private long version;


    public Loan() { }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LoanConditions getConditions() {
        return conditions;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }


    @PrePersist
    public void prePersist() {
        creationTime = DateTime.now();
    }


    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .setExcludeFieldNames("creationTime")
                .toString();
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Client client;
        private LoanConditions conditions;

        public Builder client(Client client) {
            this.client = client;
            return this;
        }

        public Builder conditions(LoanConditions conditions) {
            this.conditions = conditions;
            return this;
        }

        public Loan build() {
            Loan loan = new Loan();
            loan.setClient(this.client);
            loan.conditions = this.conditions;
            return loan;
        }

    }

}
