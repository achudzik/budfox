package io.chudzik.recruitment.budfox.clients;

import io.chudzik.recruitment.budfox.clients.dto.ClientException;
import io.chudzik.recruitment.budfox.clients.dto.ClientException.ClientNotFoundException;
import io.chudzik.recruitment.budfox.model.Loan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;


    @Transactional(readOnly = true)
    // XXX-ach: Replace with custom JSR 303 Validator
    public void validateClientExistence(Long clientId) throws ClientNotFoundException {
        Client client = clientRepository.getOne(clientId);
        if (null == client) {
            throw ClientException.notFound(clientId);
        }
    }

    public Client create(Client clientToAdd) {
        Optional.of(clientToAdd)
                .filter(AbstractPersistable::isNew)
                .orElseThrow(() -> ClientException.alreadyExists(clientToAdd));
        return clientRepository.save(clientToAdd);
    }


    public Set<Loan> loansOf(Long clientId) throws ClientNotFoundException {
        Client client = clientRepository.getClientLoans(clientId);
        if (null == client) {
            throw ClientException.notFound(clientId);
        }
        return client.getLoans();
    }


    public Client getOne(Long loanId) {
        return clientRepository.getOne(loanId);
    }

    public Client getReferenceId(Long loanId) {
        return clientRepository.getReferenceById(loanId);
    }

}
