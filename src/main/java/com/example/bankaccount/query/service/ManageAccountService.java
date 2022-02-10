package com.example.bankaccount.query.service;

import com.example.bankaccount.core.event.AccountActivatedEvent;
import com.example.bankaccount.core.event.AccountCreatedEvent;
import com.example.bankaccount.core.event.AccountCreditedEvent;
import com.example.bankaccount.core.event.AccountDebitedEvent;
import com.example.bankaccount.query.FindAccountByIdQuery;
import com.example.bankaccount.query.entity.Account;
import com.example.bankaccount.query.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageAccountService {
    private final AccountRepository accountRepository;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        log.info("Handling AccountCreatedEvent...");
        Account account = new Account();
        account.setAccountId(event.getId());
        account.setBalance(event.getBalance());
        account.setStatus("CREATED");
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("Handling AccountActivatedEvent...");
        Account account = accountRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + event.getId()));
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event) {
        log.info("Handling AccountCreditedEvent...");
        Account account = accountRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + event.getId()));
        account.setBalance(account.getBalance().add(event.getAmount()));
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event) {
        log.info("Handling AccountDebitedEvent...");
        Account account = accountRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + event.getId()));
        account.setBalance(account.getBalance().subtract(event.getAmount()));
        accountRepository.save(account);
    }

    @QueryHandler
    public Account on(FindAccountByIdQuery query) {
        log.info("Handling FindAccountByIdQuery...");
        return accountRepository.findById(query.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + query.getAccountId()));
    }
}
