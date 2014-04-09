package me.chudzik.recruitment.vivus.service.impl;

import me.chudzik.recruitment.vivus.exception.ClientNotFoundException;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ClientRepository;
import me.chudzik.recruitment.vivus.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;

public class ClientServiceImpl implements ClientService {

    private ClientRepository repository;

    @Autowired
    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public void validateClientExistence(Long clientId) throws ClientNotFoundException {
        Client client = repository.findOne(clientId);
        if (null == client) {
            throw new ClientNotFoundException(clientId);
        }
    }

}