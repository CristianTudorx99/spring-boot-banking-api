package com.tudor.bank.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tudor.bank.dto.TransactionDTO;
import com.tudor.bank.exception.GlobalExceptionHandler;
import com.tudor.bank.mapper.TransactionMapper;
import com.tudor.bank.model.Transaction;
import com.tudor.bank.model.TransactionType;
import com.tudor.bank.service.BankService;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @MockitoBean
    private TransactionMapper transactionMapper;

    @Test
    void getTransactionsForAccount_shouldReturnTransactionList() throws Exception {
        // Arrange
        Transaction transaction1 = new Transaction(TransactionType.DEPOSIT, 100);
        Transaction transaction2 = new Transaction(TransactionType.WITHDRAWAL, 50);

        TransactionDTO transactionDTO1 = new TransactionDTO(TransactionType.DEPOSIT, 100);
        TransactionDTO transactionDTO2 = new TransactionDTO(TransactionType.WITHDRAWAL, 50);

        when(bankService.getTransactionsForAccount(1001))
                .thenReturn(List.of(transaction1, transaction2));

        when(transactionMapper.toDTO(transaction1)).thenReturn(transactionDTO1);
        when(transactionMapper.toDTO(transaction2)).thenReturn(transactionDTO2);

        // Act + Assert
        mockMvc.perform(get("/bank/accounts/1001/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[1].amount").value(50.0));

        verify(bankService).getTransactionsForAccount(1001);
        verify(transactionMapper).toDTO(transaction1);
        verify(transactionMapper).toDTO(transaction2);
    }

    @Test
    void getTransactionsForAccount_shouldReturnEmptyList_whenAccountHasNoTransactions() throws Exception {
        // Arrange
        when(bankService.getTransactionsForAccount(1001))
                .thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/bank/accounts/1001/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(bankService).getTransactionsForAccount(1001);
    }
}