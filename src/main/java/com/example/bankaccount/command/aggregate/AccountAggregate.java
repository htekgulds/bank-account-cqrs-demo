package com.example.bankaccount.command.aggregate;

import com.example.bankaccount.command.CreateAccountCommand;
import com.example.bankaccount.command.DepositMoneyCommand;
import com.example.bankaccount.command.WithdrawMoneyCommand;
import com.example.bankaccount.core.event.AccountActivatedEvent;
import com.example.bankaccount.core.event.AccountCreatedEvent;
import com.example.bankaccount.core.event.AccountCreditedEvent;
import com.example.bankaccount.core.event.AccountDebitedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Slf4j
@NoArgsConstructor
@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private BigDecimal balance;
    private String status;

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        log.info("CreateAccountCommand received.");

        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.getId(),
                command.getBalance()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("An AccountCreatedEvent occurred.");
        this.accountId = event.getId();
        this.balance = event.getBalance();
        this.status = "CREATED";

        AggregateLifecycle.apply(new AccountActivatedEvent(
                this.accountId,
                "ACTIVATED"
        ));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        log.info("An AccountActivatedEvent occurred.");
        this.status = event.getStatus();
    }

    @CommandHandler
    public void on(DepositMoneyCommand command) {
        log.info("DepositMoneyCommand received.");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount()
        ));

    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        log.info("An AccountCreditedEvent occurred.");
        this.balance = this.balance.add(event.getAmount());
    }

    @CommandHandler
    public void on(WithdrawMoneyCommand command) {
        log.info("WithdrawMoneyCommand received.");
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount()
        ));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        log.info("An AccountDebitedEvent occurred.");
        this.balance = this.balance.subtract(event.getAmount());
    }
}
