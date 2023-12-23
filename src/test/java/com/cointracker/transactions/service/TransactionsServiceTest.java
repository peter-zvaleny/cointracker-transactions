package com.cointracker.transactions.service;

import com.cointracker.transactions.client.BlockchainClient;
import com.cointracker.transactions.model.Transaction;
import com.cointracker.transactions.repository.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceTest {

  @Mock
  private BlockchainClient blockchainClient;

  @Mock
  private TransactionsRepository transactionsRepository;

  @InjectMocks
  private TransactionsService transactionsService;

  private final String address = "test-address";
  private final List<Transaction> response1 = List.of(new Transaction(), new Transaction());
  private final List<Transaction> response2 = List.of(new Transaction());
  private final List<Transaction> response3 = List.of();
  private final Duration asyncWait = Duration.ofSeconds(1);

  @BeforeEach
  public void beforeEach() {
    lenient().when(blockchainClient.fetchTransactions(address, 0L)).thenReturn(response1);
    lenient().when(transactionsRepository.insertTransactions(response1)).thenReturn(true);
    lenient().when(blockchainClient.fetchTransactions(address, 50L)).thenReturn(response2);
    lenient().when(transactionsRepository.insertTransactions(response2)).thenReturn(true);
    lenient().when(blockchainClient.fetchTransactions(address, 100L)).thenReturn(response3);
  }

  @Test
  void submitSyncTransaction() {
    transactionsService.submitSyncTransactions(address);

    await()
        .atMost(asyncWait)
        .until(() -> {
          verify(transactionsRepository).insertTransactions(response1);
          verify(transactionsRepository).insertTransactions(response2);
          verify(transactionsRepository).insertTransactions(response3);
          return true;
        });
  }

  @Test
  void submitSyncTransactionFailedApiCall() {
    when(blockchainClient.fetchTransactions(address, 0L))
        .thenThrow(new RuntimeException("api failed"));

    transactionsService.submitSyncTransactions(address);

    await()
        .with().pollDelay(asyncWait)
        .until(() -> {
          verify(transactionsRepository, never()).insertTransactions(response1);
          return true;
        });
  }

  @Test
  void submitSyncTransactionFailedDbCall() {
    when(transactionsRepository.insertTransactions(response1))
        .thenThrow(new RuntimeException("db failed"));

    transactionsService.submitSyncTransactions(address);

    await()
        .with().pollDelay(asyncWait)
        .until(() -> {
          verify(blockchainClient, never()).fetchTransactions(address, 50L);
          return true;
        });
  }

  @Test
  void findTransactions() {
    when(transactionsRepository.findTransactions(address))
        .thenReturn(response1);

    var transactions = transactionsService.findTransactions(address);

    assertEquals(response1, transactions);
  }
}