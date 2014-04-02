package me.chudzik.recruitment.vivus.support.json;

import static org.fest.assertions.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MoneyModuleTest {

    static class MoneyWrappingObject {

        public Money moneyField;

        public MoneyWrappingObject() { }

        public MoneyWrappingObject(Money moneyField) {
            this.moneyField = moneyField;
        }
    }

    ObjectMapper sut = new ObjectMapper().registerModule(new MoneyModule());

    @Test
    public void shouldSerializeNullsToNulls() throws Exception {
        // arrange
        MoneyWrappingObject nullValuedWrapper = new MoneyWrappingObject();

        // act
        String result = sut.writeValueAsString(nullValuedWrapper);
        
        // assert
        assertThat(result).isEqualTo("{\"moneyField\":null}");
    }

    @Test
    public void shouldDeserializeNullsAsNulls() throws Exception {
        // arrange
        String nullValuedInJson = "{\"moneyField\":null}";

        // act
        MoneyWrappingObject result = sut.readValue(nullValuedInJson, MoneyWrappingObject.class);

        // assert
        assertThat(result.moneyField).isNull();
    }

    @Test
    public void shouldSerializeMoneyToCurrencyCodeAndAmount() throws Exception {
        // arrange
        Money validAmount = Money.of(CurrencyUnit.of("PLN"), new BigDecimal("3.00"));
        MoneyWrappingObject wrapper = new MoneyWrappingObject(validAmount);

        // act
        String result = sut.writeValueAsString(wrapper);

        // assert
        assertThat(result).isEqualTo("{\"moneyField\":\"PLN 3.00\"}");
    }

    @Test
    public void shouldDeserializeCurrencyCodeAndAmountToAmount() throws Exception {
        // arrange
        String validValuedWrapperInJson = "{\"moneyField\":\"PLN 3.00\"}";

        // act
        MoneyWrappingObject deserializedWrapper = sut.readValue(validValuedWrapperInJson, MoneyWrappingObject.class);
        Money result = deserializedWrapper.moneyField;

        // assert
        assertThat(result.getCurrencyUnit()).isEqualTo(CurrencyUnit.of("PLN"));
        assertThat(result.getAmount()).isEqualTo("3.00");
    }

}