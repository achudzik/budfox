package me.chudzik.recruitment.vivus.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import me.chudzik.recruitment.vivus.exception.ClientNotFoundException;
import me.chudzik.recruitment.vivus.repository.ClientRepository;
import me.chudzik.recruitment.vivus.service.impl.ClientServiceImpl;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.*;

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
