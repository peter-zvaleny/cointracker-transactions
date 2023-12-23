package com.cointracker.transactions.client;

import lombok.Data;

@Data
public class Tx {
  private String hash;
  private Long time;
  private Long result;
  private Long balance;
}
