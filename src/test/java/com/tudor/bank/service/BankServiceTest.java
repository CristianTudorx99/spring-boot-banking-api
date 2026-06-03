package com.tudor.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import com.tudor.bank.exception.DuplicateUserException;
import com.tudor.bank.exception.UserNotFoundException;
import com.tudor.bank.model.User;
import com.tudor.bank.repository.BankAccountRepository;
import com.tudor.bank.repository.TransactionRepository;
import com.tudor.bank.repository.UserRepository;

public class BankServiceTest {

    private BankService bankService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bankService = new BankService(userRepository, bankAccountRepository, transactionRepository);
    }

    @Test
    void createUser_shouldSaveUser_whenIdDoesNotExist() {
        // Arrange
        User user = new User(1, "Tudor");

        when(userRepository.existsById(1)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = bankService.createUser(user);

        //Assert
        assertEquals(1, result.getId());
        assertEquals("Tudor", result.getName());

        verify(userRepository).existsById(1);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowDuplicateUserException_whenIdAlreadyExists() {
        // Arrange
        User user = new User(1, "Tudor");

        when(userRepository.existsById(1)).thenReturn(true);

        // Act + Assert
        assertThrows(DuplicateUserException.class, () -> bankService.createUser(user));

        verify(userRepository).existsById(1);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Arrange
        User user1 = new User(1, "Tudor");
        User user2 = new User(2, "Cristian");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> result = bankService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Tudor", result.get(0).getName());
        assertEquals("Cristian", result.get(1).getName());

        verify(userRepository).findAll();
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Arrange
        User user = new User(1, "Tudor");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        User result = bankService.getUserById(1);

        // Assert
        assertEquals(1, result.getId());
        assertEquals("Tudor", result.getName());

        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> bankService.getUserById(99));

        verify(userRepository).findById(99);
    }
}
