package com.cointracker.transactions.controller;


import com.cointracker.transactions.service.TransactionsService;
import com.cointracker.transactions.model.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class TransactionsController {

  private final TransactionsService transactionsService;

  public TransactionsController(TransactionsService transactionsService) {
    this.transactionsService = transactionsService;
  }

  @GetMapping("/transactions/{address}")
  public Collection<Transaction> fetchTransactions(@PathVariable String address) {
    return transactionsService.findTransactions(address);
  }

  @PostMapping("/transactions/{address}")
  public void syncTransactions(@PathVariable String address) {
    transactionsService.submitSyncTransactions(address);
  }

}
