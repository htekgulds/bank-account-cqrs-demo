package com.example.bankaccount.command;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateAccountCommand extends BaseCommand<String> {
    private final BigDecimal balance;

    public CreateAccountCommand(String id, BigDecimal balance) {
        super(id);
        this.balance = balance;
    }
}
