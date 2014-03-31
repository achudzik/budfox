package me.chudzik.recruitment.vivus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;

    @Version
    private long version;


    public Client() { }

    public Long getId() {
        return id;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static class Builder {

        private final Client client = new Client();

        public Builder id(Long id) {
            client.id = id;
            return this;
        }

        public Builder identificationNumber(String identificationNumber) {
            client.identificationNumber = identificationNumber;
            return this;
        }

        public Client build() {
            return client;
        }

    }
}
