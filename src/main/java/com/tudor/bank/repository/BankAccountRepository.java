package com.tudor.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tudor.bank.model.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer>{
}
