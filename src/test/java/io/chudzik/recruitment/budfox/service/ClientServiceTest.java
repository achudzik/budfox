package io.chudzik.recruitment.budfox.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.repository.ClientRepository;
import io.chudzik.recruitment.budfox.service.impl.ClientServiceImpl;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ClientServiceTest {

    private ClientService sut;

    @Mock
    private ClientRepository clientRepositoryMock;

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
        doReturn(null).when(clientRepositoryMock).findOne(any(Long.class));

        // act
        sut.validateClientExistence(idOfNonExistingClient);
    }

    @Test
    public void shouldDoNothingOnExistingClient() {
        // arrange
        doReturn(VALID_CLIENT).when(clientRepositoryMock).findOne(any(Long.class));

        // act
        sut.validateClientExistence(VALID_ID);
    }

}
