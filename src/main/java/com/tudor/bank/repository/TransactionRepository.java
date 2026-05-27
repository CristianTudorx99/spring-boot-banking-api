package com.tudor.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tudor.bank.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
    List<Transaction> findByAccount_AccountNumberOrderByIdAsc(int accountNumber);
}
