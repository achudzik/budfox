package me.chudzik.recruitment.vivus.web;

import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.repository.ClientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/clients")
public class ClientsController {

    private ClientRepository clientRepository;

    @Autowired
    public ClientsController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Client add(@RequestBody Client clientToAdd) {
        Client addedClient = clientRepository.save(clientToAdd);
        return addedClient;
    }

}
