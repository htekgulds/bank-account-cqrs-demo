package com.example.bankaccount.query.web;

import com.example.bankaccount.query.FindAccountByIdQuery;
import com.example.bankaccount.query.entity.Account;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class ManageAccountController {
    private final QueryGateway queryGateway;

    @GetMapping("{id}")
    public ResponseEntity<Account> getAccount(@PathVariable String id) {
        Account account = queryGateway.query(new FindAccountByIdQuery(id), Account.class).join();

        if (account == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
