package com.tudor.bank.mapper;

import org.springframework.stereotype.Component;

import com.tudor.bank.dto.TransactionDTO;
import com.tudor.bank.model.Transaction;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getType(),
                transaction.getAmount()
                );
    }
}
