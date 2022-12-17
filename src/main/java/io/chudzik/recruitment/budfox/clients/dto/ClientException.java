package io.chudzik.recruitment.budfox.clients.dto;

import io.chudzik.recruitment.budfox.clients.Client;
import io.chudzik.recruitment.budfox.commons.dto.BusinessException;

import lombok.Getter;

@Getter
public class ClientException extends BusinessException {

    private final Long clientId;


    public ClientException(Long clientId, String message) {
        super(message);
        this.clientId = clientId;
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
