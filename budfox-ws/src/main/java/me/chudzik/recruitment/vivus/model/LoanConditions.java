package me.chudzik.recruitment.vivus.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Columns;
import org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmountAndCurrency;
import org.joda.money.Money;
import org.joda.time.DateTime;

@Embeddable
// XXX-ach: Move to package-info.java after fix to https://jira.spring.io/browse/SPR-10910
@org.hibernate.annotations.TypeDefs({
    @org.hibernate.annotations.TypeDef(
            defaultForType = Money.class,
            typeClass = PersistentMoneyAmountAndCurrency.class)
})
public class LoanConditions {

    @NotNull
    private BigDecimal interest;
    @NotNull
    @Columns(columns = { @Column(name = "currency", length = 3), @Column(name = "amount") })
    private Money amount;
    @Future
    @Column(name = "maturity_date", nullable = false)
    private DateTime maturityDate;


    public LoanConditions() { }


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
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .setExcludeFieldNames("creationTime")
                .toString();
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private BigDecimal interest;
        private Money amount;
        private DateTime maturityDate;

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
            condition.amount = amount;
            condition.interest = interest;
            condition.maturityDate = maturityDate;
            return condition;
        }

    }
}
