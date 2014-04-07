package me.chudzik.recruitment.vivus.web;

import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ClientRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;

@RestController
@RequestMapping("/clients")
public class ClientsController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);

    private ClientRepository clientRepository;

    @Autowired
    public ClientsController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Client add(@RequestBody Client clientToAdd) throws IllegalStateException {
        Preconditions.checkState(clientToAdd.getId() == null,
                "Invalid resource, use PUT /clients for client's update.");
        Client addedClient = clientRepository.save(clientToAdd);
        return addedClient;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalStateException(IllegalStateException ex) {
        LOG.warn(ex.getMessage());
    }

}
