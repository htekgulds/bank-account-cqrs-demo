package com.example.bankaccount.command.web;

import com.example.bankaccount.command.payload.CreateAccountRequest;
import com.example.bankaccount.command.payload.DepositRequest;
import com.example.bankaccount.command.payload.WithdrawalRequest;
import com.example.bankaccount.command.service.AccountCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class BankAccountController {
    private final AccountCommandService commandService;

    @PostMapping("create")
    public ResponseEntity<String> createAccount(@RequestBody CreateAccountRequest request) {
        try {
            CompletableFuture<String> response = commandService.createAccount(request);

            return new ResponseEntity<>(response.get(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest request) {
        try {
            CompletableFuture<String> response = commandService.depositToAccount(request);

            return new ResponseEntity<>("Amount credited", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("withdraw")
    public ResponseEntity<String> withdraw(@RequestBody WithdrawalRequest request) {
        try {
            CompletableFuture<String> response = commandService.withdrawFromAccount(request);

            return new ResponseEntity<>("Amount debited", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
