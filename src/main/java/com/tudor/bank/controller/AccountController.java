package com.tudor.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.tudor.bank.dto.AccountDTO;
import com.tudor.bank.dto.AmountRequest;
import com.tudor.bank.mapper.AccountMapper;
import com.tudor.bank.model.BankAccount;
import com.tudor.bank.service.BankService;
import com.tudor.bank.dto.CreateAccountRequest;


@RestController
@RequestMapping("/bank")
public class AccountController {

    private final BankService bankService;
    private final AccountMapper accountMapper;

    public AccountController(BankService bankService, AccountMapper accountMapper) {
        this.bankService = bankService;
        this.accountMapper = accountMapper;
    }

    @PostMapping("/users/{userId}/accounts")
    public AccountDTO createAccountForUser(@PathVariable int userId, @Valid @RequestBody CreateAccountRequest request) {
        BankAccount account = new BankAccount();
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        BankAccount createdAccount = bankService.createAccountForUser(userId, account);
        return accountMapper.toDTO(createdAccount);
    }

    @GetMapping("/users/{userId}/accounts")
    public List<AccountDTO> getAccountsForUser(@PathVariable int userId) {
        return bankService.getAccountsForUser(userId).stream().map(accountMapper::toDTO).toList();
    }

    @PostMapping("/accounts/{accountNumber}/deposit")
    public AccountDTO deposit(@PathVariable int accountNumber, @Valid @RequestBody AmountRequest request) {
        BankAccount updatedAccount = bankService.deposit(accountNumber, request.getAmount());
        return accountMapper.toDTO(updatedAccount);
    }

    @PostMapping("/accounts/{accountNumber}/withdraw")
    public AccountDTO withdraw(@PathVariable int accountNumber, @Valid @RequestBody AmountRequest request) {
        BankAccount updatedAccount = bankService.withdraw(accountNumber, request.getAmount());
        return accountMapper.toDTO(updatedAccount);
    }
}
