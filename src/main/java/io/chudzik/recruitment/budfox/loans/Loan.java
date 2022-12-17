package io.chudzik.recruitment.budfox.loans;

import io.chudzik.recruitment.budfox.clients.Client;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.LinkedHashSet;
import java.util.Set;
import static java.util.Optional.ofNullable;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties("new")
@Table(name = "loans")
@Entity
@ToString
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Loan extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1240565067423749483L;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "client_id", nullable = false)
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client client;
    @OneToOne(cascade = ALL)
    @JoinColumn(name = "current_conditions_id")
    private LoanConditions conditions;
    @OneToMany(cascade = ALL)
    @JoinTable(
        name = "previous_conditions",
        joinColumns = @JoinColumn(name = "loan_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private Set<LoanConditions> previousConditions = new LinkedHashSet<>();
    @Column(name = "creation_time", nullable = false)
    private DateTime creationTime;

    @SuppressWarnings("unused")
    @Version
    private long version;


    public Loan(Client client, LoanConditions conditions) {
        this.client = client;
        this.conditions = conditions;
    }


    @ToString.Include
    @EqualsAndHashCode.Include  // FIXME-ach: rethink the whole approach; good enough for now
    protected Long clientRef() {
        return ofNullable(this.client)
                .map(AbstractPersistable::getId)
                .orElse(null);
    }


    public void setCondition(LoanConditions newConditions) {
        if (null != conditions) {
            previousConditions.add(conditions);
        }
        conditions = newConditions;
        conditions.setLoan(this);
    }


    @PrePersist
    public void prePersist() {
        creationTime = DateTime.now();
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
            loan.setCondition(this.conditions);
            loan.creationTime = DateTime.now();
            return loan;
        }

    }

}
