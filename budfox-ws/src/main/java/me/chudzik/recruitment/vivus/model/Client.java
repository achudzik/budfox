package me.chudzik.recruitment.vivus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String identificationNumber;


    public Client() { }

    public Long getId() {
        return id;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
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
