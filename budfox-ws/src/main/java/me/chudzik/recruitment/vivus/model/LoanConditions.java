package me.chudzik.recruitment.vivus.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Columns;
import org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmountAndCurrency;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Objects;

@Entity
// XXX-ach: Move to package-info.java after fix to https://jira.spring.io/browse/SPR-10910
@org.hibernate.annotations.TypeDefs({
    @org.hibernate.annotations.TypeDef(
            defaultForType = Money.class,
            typeClass = PersistentMoneyAmountAndCurrency.class)
})
public class LoanConditions extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1823909710170076581L;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Loan loan;
    @NotNull
    private BigDecimal interest;
    @NotNull
    @Columns(columns = { @Column(name = "currency", length = 3), @Column(name = "amount") })
    private Money amount;
    @Future
    @Column(name = "maturity_date", nullable = false)
    private DateTime maturityDate;


    public LoanConditions() { }


    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public Money getAmount() {
        return amount;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(loanRef(loan), amount, interest, maturityDate);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        LoanConditions otherConditions = (LoanConditions) other;
        return Objects.equal(loanRef(loan), loanRef(otherConditions.loan))
                && Objects.equal(amount, otherConditions.amount)
                && Objects.equal(interest, otherConditions.interest)
                && Objects.equal(maturityDate, otherConditions.maturityDate);
    }

    private Long loanRef(Loan loan) {
        if (null == loan) {
            return 0L;
        }
        return Objects.firstNonNull(loan.getId(), 0L);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .setExcludeFieldNames("creationTime", "loan")
                .append("loan", loanRef(loan))
                .toString();
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
