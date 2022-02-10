package com.example.bankaccount.command;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class WithdrawMoneyCommand extends BaseCommand<String> {
    private final BigDecimal amount;

    public WithdrawMoneyCommand(String id, BigDecimal amount) {
        super(id);
        this.amount = amount;
    }
}
