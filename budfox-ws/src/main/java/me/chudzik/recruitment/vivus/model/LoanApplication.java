package me.chudzik.recruitment.vivus.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.money.Money;
import org.joda.time.Period;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

public class LoanApplication {

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Client client;
    private Money amount;
    private Period term;


    public Client getClient() {
        return client;
    }

    public Money getAmount() {
        return amount;
    }

    public Period getTerm() {
        return term;
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
        private Money amount;
        private Period term;

        private Builder() {}

        public Builder client(Client client) {
            this.client = client;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder term(Period term) {
            this.term = term;
            return this;
        }

        public LoanApplication build() {
            LoanApplication application = new LoanApplication();
            application.amount = this.amount;
            application.client = this.client;
            application.term = this.term;
            return application;
        }

    }

}
