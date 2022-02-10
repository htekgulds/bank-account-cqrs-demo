package com.example.bankaccount.command.payload;

import java.math.BigDecimal;

public record CreateAccountRequest(BigDecimal startingBalance) {
}
