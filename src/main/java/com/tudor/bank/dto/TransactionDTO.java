package com.tudor.bank.dto;

import com.tudor.bank.model.TransactionType;

public class TransactionDTO {
    private TransactionType type;
    private double amount;

    public TransactionDTO() {

    }

    public TransactionDTO(TransactionType type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}
