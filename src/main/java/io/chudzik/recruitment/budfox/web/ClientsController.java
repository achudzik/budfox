package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.Client;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.repository.ClientRepository;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/clients")
public class ClientsController {

    private ClientRepository clientRepository;

    @Autowired
    public ClientsController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Client add(@RequestBody Client clientToAdd) throws IllegalStateException {
        Preconditions.checkState(clientToAdd.getId() == null,
                "Invalid resource, use PUT /clients for client's update.");
        Client addedClient = clientRepository.save(clientToAdd);
        return addedClient;
    }

    @RequestMapping(value = "{id}/loans", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public Set<Loan> getLoans(@PathVariable("id") Long clientId) throws ClientNotFoundException {
        Client client = clientRepository.getClientLoans(clientId);
        if (null == client) {
            throw new ClientNotFoundException(clientId);
        }
        return client.getLoans();
    }

}
