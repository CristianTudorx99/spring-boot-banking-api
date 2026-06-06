package com.tudor.bank.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;

import com.tudor.bank.dto.UserDTO;
import com.tudor.bank.exception.GlobalExceptionHandler;
import com.tudor.bank.mapper.UserMapper;
import com.tudor.bank.model.User;
import com.tudor.bank.service.BankService;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void createUser_shouldReturnCreatedUser_whenRequestIsValid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "id": 1,
                  "name": "Tudor"
                }
                """;

        User savedUser = new User(1, "Tudor");
        UserDTO userDTO = new UserDTO(1, "Tudor");

        when(bankService.createUser(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(userDTO);

        // Act + Assert
        mockMvc.perform(post("/bank/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tudor"));

        verify(bankService).createUser(any(User.class));
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        // Arrange
        User user1 = new User(1, "Tudor");
        User user2 = new User(2, "Cristian");

        UserDTO userDTO1 = new UserDTO(1, "Tudor");
        UserDTO userDTO2 = new UserDTO(2, "Cristian");

        when(bankService.getAllUsers()).thenReturn(List.of(user1, user2));
        when(userMapper.toDTO(user1)).thenReturn(userDTO1);
        when(userMapper.toDTO(user2)).thenReturn(userDTO2);

        // Act + Assert
        mockMvc.perform(get("/bank/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Tudor"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Cristian"));

        verify(bankService).getAllUsers();
        verify(userMapper).toDTO(user1);
        verify(userMapper).toDTO(user2);
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() throws Exception {
        // Arrange
        User user = new User(1, "Tudor");
        UserDTO userDTO = new UserDTO(1, "Tudor");

        when(bankService.getUserById(1)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // Act + Assert
        mockMvc.perform(get("/bank/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tudor"));

        verify(bankService).getUserById(1);
        verify(userMapper).toDTO(user);
    }

    @Test
    void createUser_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "id": 0,
                  "name": ""
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/bank/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value("User id must be greater than zero."))
                .andExpect(jsonPath("$.name").value("Name cannot be blank."));
    }
}