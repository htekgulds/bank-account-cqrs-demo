package com.example.bankaccount.query.repository;

import com.example.bankaccount.query.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
