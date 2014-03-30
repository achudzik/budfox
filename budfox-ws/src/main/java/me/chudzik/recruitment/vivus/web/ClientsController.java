package me.chudzik.recruitment.vivus.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import me.chudzik.recruitment.vivus.model.Client;
@Controller
@RequestMapping("/clients")
public class ClientsController {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Client add(@RequestBody Client clientToAdd) {
        return clientToAdd;
    }
}
