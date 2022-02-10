package com.example.bankaccount.command.payload;

import java.math.BigDecimal;

public record WithdrawalRequest(String accountId, BigDecimal amount) {
}
