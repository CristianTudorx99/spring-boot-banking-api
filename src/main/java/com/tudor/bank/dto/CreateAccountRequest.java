package com.tudor.bank.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateAccountRequest {

    @Positive(message = "Account number must be greater than zero.")
    private int accountNumber;

    @PositiveOrZero(message = "Initial balance cannot be negative.")
    private double balance;

    public CreateAccountRequest() {
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}