package me.chudzik.recruitment.vivus.service;

import me.chudzik.recruitment.vivus.exception.ClientNotFoundException;

public interface ClientService {

    // XXX-ach: Replace with custom JSR 303 Validator
    void validateClientExistence(Long clientId) throws ClientNotFoundException;

}
