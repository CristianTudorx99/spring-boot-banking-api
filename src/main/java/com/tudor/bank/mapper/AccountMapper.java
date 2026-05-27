package com.tudor.bank.mapper;

import org.springframework.stereotype.Component;

import com.tudor.bank.dto.AccountDTO;
import com.tudor.bank.model.BankAccount;

@Component
public class AccountMapper {
    public AccountDTO toDTO(BankAccount account) {
        return new AccountDTO(
                account.getAccountNumber(),
                account.getBalance()
        );
    }
}
