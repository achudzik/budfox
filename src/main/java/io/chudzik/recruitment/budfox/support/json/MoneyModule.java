package io.chudzik.recruitment.budfox.support.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.money.Money;

import java.io.IOException;

@SuppressWarnings("serial")
public class MoneyModule extends SimpleModule {

    public static class JodaMoneySerializer extends JsonSerializer<Money> {
        @Override
        public void serialize(Money money, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeString(moneyToString(money));
        }

        public String moneyToString(Money value) {
            if (null == value) {
                return null;
            }
            String amount = value.getAmount().toString();
            String currentyCode = value.getCurrencyUnit().getCurrencyCode();
            return String.format("%s %s", currentyCode, amount);
        }

    }

    public static class JodaMoneyDeserializer extends JsonDeserializer<Money> {
        @Override
        public Money deserialize(JsonParser parser, DeserializationContext context)
                throws IOException, JsonProcessingException {
            return moneyFromString(parser.getValueAsString());
        }

        public Money moneyFromString(String moneyString) {
            if (null == moneyString) {
                return null;
            }
            return Money.parse(moneyString);
        }

    }

    public static final JsonSerializer<Money> MONEY_SERIALIZER = new JodaMoneySerializer();
    public static final JsonDeserializer<Money> MONEY_DESERIALIZER = new JodaMoneyDeserializer();


    public MoneyModule() {
        addSerializer(Money.class, MONEY_SERIALIZER);
        addDeserializer(Money.class, MONEY_DESERIALIZER);
    }

}
