package com.tudor.bank.controller;

import static org.mockito.ArgumentMatchers.any;
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

import com.tudor.bank.dto.AccountDTO;
import com.tudor.bank.exception.GlobalExceptionHandler;
import com.tudor.bank.mapper.AccountMapper;
import com.tudor.bank.model.BankAccount;
import com.tudor.bank.service.BankService;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @MockitoBean
    private AccountMapper accountMapper;

    @Test
    void createAccountForUser_shouldReturnCreatedAccount_whenRequestIsValid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "accountNumber": 1001,
                  "balance": 500
                }
                """;

        BankAccount createdAccount = new BankAccount();
        createdAccount.setAccountNumber(1001);
        createdAccount.setBalance(500);

        AccountDTO accountDTO = new AccountDTO(1001, 500);

        when(bankService.createAccountForUser(org.mockito.ArgumentMatchers.eq(1), any(BankAccount.class)))
                .thenReturn(createdAccount);
        when(accountMapper.toDTO(createdAccount)).thenReturn(accountDTO);

        // Act + Assert
        mockMvc.perform(post("/bank/users/1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(1001))
                .andExpect(jsonPath("$.balance").value(500.0));

        verify(bankService).createAccountForUser(org.mockito.ArgumentMatchers.eq(1), any(BankAccount.class));
        verify(accountMapper).toDTO(createdAccount);
    }

    @Test
    void getAccountsForUser_shouldReturnAccountList() throws Exception {
        // Arrange
        BankAccount account1 = new BankAccount();
        account1.setAccountNumber(1001);
        account1.setBalance(500);

        BankAccount account2 = new BankAccount();
        account2.setAccountNumber(1002);
        account2.setBalance(1000);

        AccountDTO accountDTO1 = new AccountDTO(1001, 500);
        AccountDTO accountDTO2 = new AccountDTO(1002, 1000);

        when(bankService.getAccountsForUser(1)).thenReturn(List.of(account1, account2));
        when(accountMapper.toDTO(account1)).thenReturn(accountDTO1);
        when(accountMapper.toDTO(account2)).thenReturn(accountDTO2);

        // Act + Assert
        mockMvc.perform(get("/bank/users/1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(1001))
                .andExpect(jsonPath("$[0].balance").value(500.0))
                .andExpect(jsonPath("$[1].accountNumber").value(1002))
                .andExpect(jsonPath("$[1].balance").value(1000.0));

        verify(bankService).getAccountsForUser(1);
        verify(accountMapper).toDTO(account1);
        verify(accountMapper).toDTO(account2);
    }

    @Test
    void deposit_shouldReturnUpdatedAccount_whenAmountIsValid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "amount": 100
                }
                """;

        BankAccount updatedAccount = new BankAccount();
        updatedAccount.setAccountNumber(1001);
        updatedAccount.setBalance(600);

        AccountDTO accountDTO = new AccountDTO(1001, 600);

        when(bankService.deposit(1001, 100)).thenReturn(updatedAccount);
        when(accountMapper.toDTO(updatedAccount)).thenReturn(accountDTO);

        // Act + Assert
        mockMvc.perform(post("/bank/accounts/1001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(1001))
                .andExpect(jsonPath("$.balance").value(600.0));

        verify(bankService).deposit(1001, 100);
        verify(accountMapper).toDTO(updatedAccount);
    }

    @Test
    void withdraw_shouldReturnUpdatedAccount_whenAmountIsValid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "amount": 50
                }
                """;

        BankAccount updatedAccount = new BankAccount();
        updatedAccount.setAccountNumber(1001);
        updatedAccount.setBalance(450);

        AccountDTO accountDTO = new AccountDTO(1001, 450);

        when(bankService.withdraw(1001, 50)).thenReturn(updatedAccount);
        when(accountMapper.toDTO(updatedAccount)).thenReturn(accountDTO);

        // Act + Assert
        mockMvc.perform(post("/bank/accounts/1001/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(1001))
                .andExpect(jsonPath("$.balance").value(450.0));

        verify(bankService).withdraw(1001, 50);
        verify(accountMapper).toDTO(updatedAccount);
    }

    @Test
    void createAccountForUser_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "accountNumber": 0,
                  "balance": -500
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/bank/users/1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountNumber").value("Account number must be greater than zero."))
                .andExpect(jsonPath("$.balance").value("Initial balance cannot be negative."));
    }

    @Test
    void deposit_shouldReturnBadRequest_whenAmountIsInvalid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "amount": -100
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/bank/accounts/1001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be greater than zero."));
    }

    @Test
    void withdraw_shouldReturnBadRequest_whenAmountIsInvalid() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "amount": -100
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/bank/accounts/1001/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be greater than zero."));
    }
}