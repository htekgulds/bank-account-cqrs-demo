package com.example.bankaccount.command.service;

import com.example.bankaccount.command.CreateAccountCommand;
import com.example.bankaccount.command.DepositMoneyCommand;
import com.example.bankaccount.command.WithdrawMoneyCommand;
import com.example.bankaccount.command.payload.CreateAccountRequest;
import com.example.bankaccount.command.payload.DepositRequest;
import com.example.bankaccount.command.payload.WithdrawalRequest;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final CommandGateway commandGateway;

    public CompletableFuture<String> createAccount(CreateAccountRequest request) {
        return commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.startingBalance()
        ));
    }

    public CompletableFuture<String> depositToAccount(DepositRequest request) {
        return commandGateway.send(new DepositMoneyCommand(
                request.accountId(),
                request.amount()
        ));
    }

    public CompletableFuture<String> withdrawFromAccount(WithdrawalRequest request) {
        return commandGateway.send(new WithdrawMoneyCommand(
                request.accountId(),
                request.amount()
        ));
    }
}
