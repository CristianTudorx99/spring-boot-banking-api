package com.tudor.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tudor.bank.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
