package com.example.bankaccount.command.payload;

import java.math.BigDecimal;

public record DepositRequest(String accountId, BigDecimal amount) {
}
