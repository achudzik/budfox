package io.chudzik.recruitment.budfox.commons.web.json

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.commons.web.json.MoneyModule
import io.chudzik.recruitment.budfox.config.WebLayerConfiguration.JsonMappingConfiguration

import com.fasterxml.jackson.databind.ObjectMapper
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import spock.lang.Subject

@Subject(MoneyModule)
class MoneyModuleSpec extends BaseUnitSpec {

    ObjectMapper sut = JsonMappingConfiguration.objectMapper()


    def "should serialize nulls to nulls"() {
        given:
            MoneyWrappingObject nullValuedWrapper = new MoneyWrappingObject()
        when:
            String result = sut.writeValueAsString(nullValuedWrapper)
        then:
            result == '''{"moneyField":null}'''
    }


    def "should deserialize nulls as nulls"() {
        given:
            String nullValuedInJson = '''{"moneyField":null}'''
        when:
            MoneyWrappingObject result = sut.readValue(nullValuedInJson, MoneyWrappingObject.class)
        then:
            result.moneyField == null
    }


    def "should serialize money to currency code and amount"() {
        given:
            Money validAmount = Money.of(CurrencyUnit.of("PLN"), new BigDecimal("3.00"))
            MoneyWrappingObject wrapper = new MoneyWrappingObject(validAmount)
        expect:
            '''{"moneyField":"PLN 3.00"}''' == sut.writeValueAsString(wrapper)
    }


    def "should deserialize currency code and amount to amount"() {
        given:
            String validValuedWrapperInJson = '''{"moneyField":"PLN 3.00"}'''
        when:
            MoneyWrappingObject deserializedWrapper = sut.readValue(validValuedWrapperInJson, MoneyWrappingObject.class)
            Money result = deserializedWrapper.moneyField
        then:
            result.currencyUnit == CurrencyUnit.of("PLN")
            result.amount == new BigDecimal("3.00")
    }


    static class MoneyWrappingObject {

        public Money moneyField

        MoneyWrappingObject() {}

        MoneyWrappingObject(Money moneyField) {
            this.moneyField = moneyField
        }

    }

}
