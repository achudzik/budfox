package io.chudzik.recruitment.budfox.service.impl;

import io.chudzik.recruitment.budfox.exception.ClientException;
import io.chudzik.recruitment.budfox.exception.ClientException.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.service.ClientService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;


    @Transactional(readOnly = true)
    @Override
    public void validateClientExistence(Long clientId) throws ClientNotFoundException {
        Client client = repository.getOne(clientId);
        if (null == client) {
            throw ClientException.notFound(clientId);
        }
    }

}
