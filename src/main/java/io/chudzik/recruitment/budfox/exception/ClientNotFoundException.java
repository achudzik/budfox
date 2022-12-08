package io.chudzik.recruitment.budfox.exception;

public class ClientNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7035129573030300269L;

    private Long clientId;

    public ClientNotFoundException(Long clientId) {
        super("Client with given ID not found.");
        this.clientId = clientId;
    }

    public Long getClientId() {
        return clientId;
    }

}
