package io.chudzik.recruitment.budfox.exception;

import io.chudzik.recruitment.budfox.model.Client;

public class ClientException extends BusinessException {

    private Long clientId;


    public ClientException(Long clientId, String message) {
        super(message);
        this.clientId = clientId;
    }


    public Long getClientId() {
        return clientId;
    }


    public static ClientException notFound(Long clientId) {
        return new ClientNotFoundException(clientId);
    }

    public static ClientException alreadyExists(Client client) {
        return new ClientAlreadyExistsException(client.getId());
    }


    public static class ClientAlreadyExistsException extends ClientException {

        public ClientAlreadyExistsException(Long clientId) {
            this(clientId, "Such client already exists.");
        }
        public ClientAlreadyExistsException(Long clientId, String message) {
            super(clientId, message);
        }

    }

    public static class ClientNotFoundException extends ClientException {

        public ClientNotFoundException(Long clientId) {
            this(clientId, "Client with given ID not found.");
        }
        public ClientNotFoundException(Long clientId, String message) {
            super(clientId, message);
        }

    }

}
