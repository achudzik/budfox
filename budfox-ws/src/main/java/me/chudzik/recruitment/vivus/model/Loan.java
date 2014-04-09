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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Objects;

@Entity
@Table(name = "loans")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties("new")
public class Loan extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1240565067423749483L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
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
    public int hashCode() {
        return Objects.hashCode(client, conditions);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Loan otherLoan = (Loan) other;
        return Objects.equal(client, otherLoan.client)
                && Objects.equal(conditions, otherLoan.conditions);
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

        private Long id;
        private Client client;
        private LoanConditions conditions;

        private Builder() { }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        // XXX-ach: move to Loan.TestDataBuilder - should be only possible to set through Client.addLoan()
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
            loan.setId(this.id);
            loan.setClient(this.client);
            loan.conditions = this.conditions;
            loan.creationTime = DateTime.now();
            return loan;
        }

    }

}
