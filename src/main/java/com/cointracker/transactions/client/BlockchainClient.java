package com.cointracker.transactions.client;

import com.cointracker.transactions.model.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class BlockchainClient {

  private static final String URL = "https://blockchain.info";
  private static final String RAWADDR_TEMPLATE = URL + "/rawaddr/{address}?limit=50&offset={offset}";

  private final RestTemplate restTemplate;

  public BlockchainClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Collection<Transaction> fetchTransactions(String address, Long offset) {
    var raw = restTemplate.getForObject(RAWADDR_TEMPLATE, RawAddr.class, address, offset);

    return raw.getTxs()
        .stream()
        .map(tx -> txToTransaction(address, tx))
        .collect(Collectors.toList());
  }

  private static Transaction txToTransaction(String address, Tx tx) {
    var transaction = new Transaction();
    transaction.setAddress(address);
    transaction.setHash(tx.getHash());
    transaction.setTime(tx.getTime());
    transaction.setResult(tx.getResult());
    transaction.setBalance(tx.getBalance());
    return transaction;
  }

}
