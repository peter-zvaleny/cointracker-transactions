package com.cointracker.transactions.service;

import com.cointracker.transactions.client.BlockchainClient;
import com.cointracker.transactions.model.Transaction;
import com.cointracker.transactions.repository.TransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TransactionsService {

  private static final long PAGE_SIZE = 50;
  private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

  private final BlockchainClient blockchainClient;
  private final TransactionsRepository transactionsRepository;

  public TransactionsService(BlockchainClient blockchainClient,
                             TransactionsRepository transactionsRepository) {
    this.blockchainClient = blockchainClient;
    this.transactionsRepository = transactionsRepository;
  }

  public void submitSyncTransactions(String address) {
    POOL.submit(() -> {
      try {
        syncTransactions(address);
      } catch (RuntimeException ex) {
        log.error("failed transaction sync for address: {}", address, ex);
      }
    });
  }

  private void syncTransactions(String address) {
    var offset = 0L;
    var page = 0;
    Collection<Transaction> txs;
    boolean allInserted;
    do {
      txs = blockchainClient.fetchTransactions(address, offset);
      allInserted = transactionsRepository.insertTransactions(txs);
      page++;
      offset = page * PAGE_SIZE;
    } while (!txs.isEmpty() && allInserted && page < 3);
    // todo remove page limit
  }

  public Collection<Transaction> findTransactions(String address) {
    return transactionsRepository.findTransactions(address);
  }
}
