package me.chudzik.recruitment.vivus.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

@Entity
@Table(name = "clients")
@JsonIgnoreProperties("new")
public class Client extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -8738990609586826604L;

    @NotNull
    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Loan> loans = new HashSet<>();

    @SuppressWarnings("unused")
    @Version
    private long version;


    public Client() { }


    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public Set<Loan> getLoans() {
        return Collections.unmodifiableSet(loans);
    }


    public void addLoan(Loan loan) {
        checkNotNull(loan, "Cannot be null loan");
        checkState(loan.getClient() == null, "Loan is already assigned to a Client");

        loans.add(loan);
        loan.setClient(this);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(identificationNumber);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Client otherClient = (Client) other;
        return Objects.equal(identificationNumber, otherClient.identificationNumber);
    }


    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String identificationNumber;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder identificationNumber(String identificationNumber) {
            this.identificationNumber = identificationNumber;
            return this;
        }

        public Client build() {
            Client client = new Client();
            client.setId(this.id);
            client.identificationNumber = this.identificationNumber;
            return client;
        }

    }

}
