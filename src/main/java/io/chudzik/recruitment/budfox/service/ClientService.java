package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;

public interface ClientService {

    // XXX-ach: Replace with custom JSR 303 Validator
    void validateClientExistence(Long clientId) throws ClientNotFoundException;

}
