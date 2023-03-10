package io.chudzik.recruitment.budfox.clients;

import io.chudzik.recruitment.budfox.loans.Loan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties("new")
@Table(name = "clients")
@Entity
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
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


    public void addLoan(Loan loan) {
        Preconditions.checkNotNull(loan, "Cannot be null loan");
        Preconditions.checkState(loan.getClient() == null, "Loan is already assigned to a Client");

        loans.add(loan);
        loan.setClient(this);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String identificationNumber;
        private List<Loan> loans = new ArrayList<>();

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder identificationNumber(String identificationNumber) {
            this.identificationNumber = identificationNumber;
            return this;
        }

        public Builder withLoan(Loan loan) {
            loans.add(loan);
            return this;
        }

        public Client build() {
            Client client = new Client();
            client.setId(this.id);
            addLoansTo(client);
            client.identificationNumber = this.identificationNumber;
            return client;
        }

        private void addLoansTo(Client client) {
            for (Loan loan : this.loans) {
                client.addLoan(loan);
            }
        }
    }

}
