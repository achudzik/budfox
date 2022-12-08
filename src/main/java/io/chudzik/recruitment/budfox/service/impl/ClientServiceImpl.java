package io.chudzik.recruitment.budfox.service.impl;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository repository;

    @Autowired
    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public void validateClientExistence(Long clientId) throws ClientNotFoundException {
        Client client = repository.findOne(clientId);
        if (null == client) {
            throw new ClientNotFoundException(clientId);
        }
    }

}
