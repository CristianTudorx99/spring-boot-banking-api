package com.tudor.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tudor.bank.dto.TransactionDTO;
import com.tudor.bank.mapper.TransactionMapper;
import com.tudor.bank.service.BankService;

@RestController
@RequestMapping("/bank/accounts")
public class TransactionController {

    private final BankService bankService;
    private final TransactionMapper transactionMapper;

    public TransactionController(BankService bankService, TransactionMapper transactionMapper) {
        this.bankService = bankService;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionDTO> getTransactionsForAccount(@PathVariable int accountNumber) {
        return bankService.getTransactionsForAccount(accountNumber)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }
}
