package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.ClientException.ClientNotFoundException;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.service.impl.ClientServiceImpl;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId;

public class ClientServiceTest {

    @Mock ClientRepository clientRepositoryMock;

    ClientService sut;


    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new ClientServiceImpl(clientRepositoryMock);
    }


    @Test(expectedExceptions = ClientNotFoundException.class,
        expectedExceptionsMessageRegExp = "Client with given ID not found.")
    public void shouldThrowExceptionOnNonExistingClient() {
        // arrange
        Long idOfNonExistingClient = new Long(1844);
        doReturn(null).when(clientRepositoryMock).getOne(any(Long.class));

        // act
        sut.validateClientExistence(idOfNonExistingClient);
    }


    @Test
    public void shouldDoNothingOnExistingClient() {
        // arrange
        doReturn(VALID_CLIENT).when(clientRepositoryMock).getOne(any(Long.class));

        // act
        sut.validateClientExistence(validId());
    }

}
