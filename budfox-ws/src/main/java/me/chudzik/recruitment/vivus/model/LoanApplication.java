package me.chudzik.recruitment.vivus.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.money.Money;
import org.joda.time.Period;

public class LoanApplication {

    private Client client;
    private Money amount;
    private Period term;


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
            application.setClient(this.client);
            application.term = this.term;
            return application;
        }

    }

}
