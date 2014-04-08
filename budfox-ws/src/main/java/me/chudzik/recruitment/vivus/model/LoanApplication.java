package me.chudzik.recruitment.vivus.model;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.google.common.base.Objects;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class LoanApplication {

    /* TODO-ach: in Jackson 2.4 will be added support for ObjectIdResolver, which will allow deserialization to objects solely on base of id (but ONLY with it).
     * Details: https://github.com/FasterXML/jackson-databind/issues/138
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Client client;
     */
    @NotNull
    @Min(0)
    private Long clientId;
    @NotNull
    private Money amount;
    @Future
    private DateTime maturityDate;
    @NotNull
    private Period term;

    public Long getClientId() {
        return clientId;
    }

    /**
     * @return client with only id property set
     */
    public Client getClient() {
        // TODO-ach: replace with client constructed internally by Jackson
        return Client.builder().id(clientId).build();
    }

    public Money getAmount() {
        return amount;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public Period getTerm() {
        return term;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(clientId, amount, maturityDate, term);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LoanApplication otherApplication = (LoanApplication) other;
        return Objects.equal(clientId, otherApplication.clientId)
                && Objects.equal(amount, otherApplication.amount)
                && Objects.equal(maturityDate, otherApplication.maturityDate)
                && Objects.equal(term, otherApplication.term);
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
        private DateTime maturityDate;
        private Period term;

        private Builder() {}

        public Builder client(Client client) {
            this.client = client;
            return this;
        }

        public Builder clientId(Long clientId) {
            this.client = Client.builder().id(clientId).build();
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

        public Builder term(Period term) {
            this.term = term;
            return this;
        }

        public LoanApplication build() {
            LoanApplication application = new LoanApplication();
            application.amount = this.amount;
            // TODO-ach: change from Long to Client after introducing above mentioned resolver
            application.clientId = this.client.getId();
            application.maturityDate = this.maturityDate;
            application.term = this.term;
            return application;
        }

    }

}
