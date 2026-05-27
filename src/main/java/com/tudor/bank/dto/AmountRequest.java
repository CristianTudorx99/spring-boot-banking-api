package com.tudor.bank.dto;

import jakarta.validation.constraints.Positive;

public class AmountRequest {

    @Positive(message = "Amount must be grater than zero.")
    private double amount;

    public AmountRequest() {}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


}
