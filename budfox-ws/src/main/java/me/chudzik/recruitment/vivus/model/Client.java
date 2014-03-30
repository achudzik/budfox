package me.chudzik.recruitment.vivus.model;

public class Client {

    private Long id;
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

        public Builder identificationNumber(String identificationNumber) {
            client.identificationNumber = identificationNumber;
            return this;
        }

        public Client build() {
            return client;
        }

    }
}
