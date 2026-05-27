package com.tudor.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.tudor.bank.model.User;
import com.tudor.bank.service.BankService;
import com.tudor.bank.dto.UserDTO;
import com.tudor.bank.mapper.UserMapper;
import com.tudor.bank.dto.CreateUserRequest;

@RestController
@RequestMapping("/bank/users")
public class UserController {

    private final BankService bankService;
    private final UserMapper userMapper;

    public UserController(BankService bankService, UserMapper userMapper) {
        this.bankService = bankService;
        this.userMapper = userMapper;
    }

    @PostMapping()
    public UserDTO createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = new User(request.getId(), request.getName());
        User createdUser = bankService.createUser(user);
        return userMapper.toDTO(user);
    }

    @GetMapping()
    public List<UserDTO> getAllUsers() {
        return bankService.getAllUsers()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDTO getUserByID(@PathVariable int id) {
        User user = bankService.getUserById(id);
        return userMapper.toDTO(user);
    }
}
