package com.tudor.bank.service;

import com.tudor.bank.exception.*;
import com.tudor.bank.model.BankAccount;
import com.tudor.bank.model.Transaction;
import com.tudor.bank.model.TransactionType;
import com.tudor.bank.model.User;
import com.tudor.bank.repository.BankAccountRepository;
import com.tudor.bank.repository.TransactionRepository;
import com.tudor.bank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BankService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public BankService(UserRepository userRepository, BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    public User createUser(User user) {
            if (userRepository.existsById(user.getId())) {
                throw new DuplicateUserException("User with id " + user.getId() + " already exists.");
            }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
    }

    public BankAccount createAccountForUser(int userId, BankAccount account) {
        if (bankAccountRepository.existsById(account.getAccountNumber())) {
            throw new DuplicateAccountException("Account with number " + account.getAccountNumber() + " already exists.");
        }

        User user = getUserById(userId);
        user.addAccount(account);

        bankAccountRepository.save(account);

        return account;
    }

    public List<BankAccount> getAccountsForUser(int userId) {
        User user = getUserById(userId);
        return user.getAccounts();
    }

    public BankAccount findAccountByNumber(int accountNumber) {
        return bankAccountRepository.findById(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account " + accountNumber + " not found."));
    }

    public BankAccount deposit(int accountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be grater than zero.");
        }

        BankAccount account = findAccountByNumber(accountNumber);
        account.setBalance(account.getBalance() + amount);
        account.addTransactions(new Transaction(TransactionType.DEPOSIT, amount));

        return bankAccountRepository.save(account);
    }

    public BankAccount withdraw(int accountNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be grater than zero.");
        }

        BankAccount account = findAccountByNumber(accountNumber);

        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for account " + account.getAccountNumber());
        }

        account.setBalance(account.getBalance() - amount);
        account.addTransactions(new Transaction(TransactionType.WITHDRAWAL, amount));

        return bankAccountRepository.save(account);
    }

    public List<Transaction> getTransactionsForAccount(int accountNumber) {
        findAccountByNumber(accountNumber);
        return transactionRepository.findByAccount_AccountNumberOrderByIdAsc(accountNumber);
    }
}