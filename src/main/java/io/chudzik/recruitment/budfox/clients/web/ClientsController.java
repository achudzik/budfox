package io.chudzik.recruitment.budfox.clients.web;

import io.chudzik.recruitment.budfox.clients.Client;
import io.chudzik.recruitment.budfox.clients.ClientService;
import io.chudzik.recruitment.budfox.clients.dto.ClientException;
import io.chudzik.recruitment.budfox.loans.Loan;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
class ClientsController {

    private final ClientService clientService;


    @ResponseStatus(CREATED)
    @PostMapping(path = "/clients", consumes = APPLICATION_JSON_VALUE)
    public Client add(@RequestBody Client clientToAdd) throws ClientException {
        return clientService.create(clientToAdd);
    }


    @ResponseStatus(OK)
    @GetMapping(path = "/clients/{id}/loans")
    public Set<Loan> getLoans(@PathVariable("id") Long clientId) throws ClientException {
        return clientService.loansOf(clientId);
    }

}
