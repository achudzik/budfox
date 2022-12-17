package io.chudzik.recruitment.budfox.loans;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Columns;
import org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmountAndCurrency;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import static java.util.Optional.ofNullable;
import static javax.persistence.CascadeType.ALL;

// XXX-ach: Move to package-info.java after fix to https://jira.spring.io/browse/SPR-10910
@org.hibernate.annotations.TypeDefs({
    @org.hibernate.annotations.TypeDef(
            defaultForType = Money.class,
            typeClass = PersistentMoneyAmountAndCurrency.class)
})
@Entity
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class LoanConditions extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1823909710170076581L;

    @ManyToOne(cascade = ALL)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Setter
    private Loan loan;
    @NotNull
    private BigDecimal interest;
    @NotNull
    @Columns(columns = { @Column(name = "currency", length = 3), @Column(name = "amount") })
    private Money amount;
    @Future
    @Column(name = "maturity_date", nullable = false)
    private DateTime maturityDate;


    @ToString.Include
    @EqualsAndHashCode.Include  // FIXME-ach: rethink the whole approach; good enough for now
    protected Long loanRef() {
        return ofNullable(this.loan)
                .map(AbstractPersistable::getId)
                .orElse(null);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private BigDecimal interest;
        private Money amount;
        private DateTime maturityDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder interest(BigDecimal interest) {
            this.interest = interest;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder maturityDate(DateTime maturityDate) {
            this.maturityDate = maturityDate;
            return this;
        }

        public LoanConditions build() {
            LoanConditions condition = new LoanConditions();
            condition.setId(this.id);
            condition.amount = this.amount;
            condition.interest = this.interest;
            condition.maturityDate = this.maturityDate;
            return condition;
        }

    }

}
