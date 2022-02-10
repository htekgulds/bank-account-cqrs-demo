package com.example.bankaccount.core.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AccountCreatedEvent extends BaseEvent<String> {
    private final BigDecimal balance;

    public AccountCreatedEvent(String id, BigDecimal balance) {
        super(id);
        this.balance = balance;
    }
}
