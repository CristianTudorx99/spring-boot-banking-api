package com.tudor.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateUserRequest {

    @Positive(message = "user id must be grater than zero. ")
    private int id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    public CreateUserRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}