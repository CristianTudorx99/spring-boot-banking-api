package com.tudor.bank.mapper;

import org.springframework.stereotype.Component;

import com.tudor.bank.dto.UserDTO;
import com.tudor.bank.model.User;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName());
    }
}
