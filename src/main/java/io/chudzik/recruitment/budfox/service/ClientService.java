package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.ClientException;
import io.chudzik.recruitment.budfox.exception.ClientException.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;


    @Transactional(readOnly = true)
    // XXX-ach: Replace with custom JSR 303 Validator
    public void validateClientExistence(Long clientId) throws ClientNotFoundException {
        Client client = repository.getOne(clientId);
        if (null == client) {
            throw ClientException.notFound(clientId);
        }
    }

}
