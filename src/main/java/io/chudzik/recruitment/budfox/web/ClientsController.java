package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.exception.ClientException;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class ClientsController {

    private final ClientRepository clientRepository;


    @ResponseStatus(CREATED)
    @PostMapping(path = "/clients", consumes = APPLICATION_JSON_VALUE)
    public Client add(@RequestBody Client clientToAdd) throws ClientException {
        Optional.of(clientToAdd)
                .filter(AbstractPersistable::isNew)
                .orElseThrow(() -> ClientException.alreadyExists(clientToAdd));
        Client addedClient = clientRepository.save(clientToAdd);
        return addedClient;
    }


    @ResponseStatus(OK)
    @GetMapping(path = "/clients/{id}/loans")
    public Set<Loan> getLoans(@PathVariable("id") Long clientId) throws ClientException {
        Client client = clientRepository.getClientLoans(clientId);
        if (null == client) {
            throw ClientException.notFound(clientId);
        }
        return client.getLoans();
    }

}
