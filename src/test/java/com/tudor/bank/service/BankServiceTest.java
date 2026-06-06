package com.tudor.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.anyInt;

import java.util.List;
import java.util.Optional;

import com.tudor.bank.exception.AccountNotFoundException;
import com.tudor.bank.exception.DuplicateAccountException;
import com.tudor.bank.exception.DuplicateUserException;
import com.tudor.bank.exception.InsufficientBalanceException;
import com.tudor.bank.exception.InvalidAmountException;
import com.tudor.bank.exception.UserNotFoundException;
import com.tudor.bank.model.BankAccount;
import com.tudor.bank.model.Transaction;
import com.tudor.bank.model.TransactionType;
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

    @Test
    void createAccountForUser_shouldSaveAccount_whenAccountNumberDoesNotExist() {
        // Arrange
        User user = new User(1, "Tudor");

        BankAccount account = new BankAccount();
        account.setAccountNumber(1001);
        account.setBalance(500);

        when(bankAccountRepository.existsById(1001)).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bankAccountRepository.save(account)).thenReturn(account);

        // Act
        BankAccount result = bankService.createAccountForUser(1, account);

        // Assert
        assertEquals(1001, result.getAccountNumber());
        assertEquals(500, result.getBalance());
        assertEquals(user, result.getUser());

        verify(bankAccountRepository).existsById(1001);
        verify(userRepository).findById(1);
        verify(bankAccountRepository).save(account);
    }

    @Test
    void createAccountForUser_shouldThrowDuplicateAccountException_whenAccountNumberAlreadyExists() {
        // Arrange
        BankAccount account = new BankAccount();
        account.setAccountNumber(1001);
        account.setBalance(500);

        when(bankAccountRepository.existsById(1001)).thenReturn(true);

        // Act + Assert
        assertThrows(DuplicateAccountException.class, () -> bankService.createAccountForUser(1, account));

        verify(bankAccountRepository).existsById(1001);
        verify(userRepository, never()).findById(anyInt());
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void deposit_shouldIncreaseBalanceAndCreateDepositTransaction_whenAmountIsValid() {
        // Arrange
        BankAccount account = new BankAccount();
        account.setAccountNumber(1001);
        account.setBalance(500);

        when(bankAccountRepository.findById(1001)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(account)).thenReturn(account);

        // Act
        BankAccount result = bankService.deposit(1001, 100);

        // Assert
        assertEquals(600, result.getBalance());
        assertEquals(1, result.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, result.getTransactions().get(0).getType());
        assertEquals(100, result.getTransactions().get(0).getAmount());

        verify(bankAccountRepository).findById(1001);
        verify(bankAccountRepository).save(account);
    }

    @Test
    void withdraw_shouldDecreaseBalanceAndCreateWithdrawalTransaction_whenAmountIsValid() {
        // Arrange
        BankAccount account = new BankAccount();
        account.setAccountNumber(1001);
        account.setBalance(500);

        when(bankAccountRepository.findById(1001)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(account)).thenReturn(account);

        // Act
        BankAccount result = bankService.withdraw(1001, 100);

        // Assert
        assertEquals(400, result.getBalance());
        assertEquals(1, result.getTransactions().size());
        assertEquals(TransactionType.WITHDRAWAL, result.getTransactions().get(0).getType());
        assertEquals(100, result.getTransactions().get(0).getAmount());

        verify(bankAccountRepository).findById(1001);
        verify(bankAccountRepository).save(account);
    }

    @Test
    void withdraw_shouldThrowInsufficientBalanceException_whenAmountIsGreaterThanBalance() {
        // Arrange
        BankAccount account = new BankAccount();
        account.setAccountNumber(1001);
        account.setBalance(50);

        when(bankAccountRepository.findById(1001)).thenReturn(Optional.of(account));

        // Act + Assert
        assertThrows(InsufficientBalanceException.class, () -> bankService.withdraw(1001, 100));

        verify(bankAccountRepository).findById(1001);
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void deposit_shouldThrowInvalidAmountException_whenAmountIsZeroOrNegative() {
        // Act + Assert
        assertThrows(InvalidAmountException.class, () -> bankService.deposit(1001, 0));
        assertThrows(InvalidAmountException.class, () -> bankService.deposit(1001, -100));

        verify(bankAccountRepository, never()).findById(anyInt());
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void withdraw_shouldThrowInvalidAmountException_whenAmountIsZeroOrNegative() {
        // Act + Assert
        assertThrows(InvalidAmountException.class, () -> bankService.withdraw(1001, 0));
        assertThrows(InvalidAmountException.class, () -> bankService.withdraw(1001, -100));

        verify(bankAccountRepository, never()).findById(anyInt());
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }
}
