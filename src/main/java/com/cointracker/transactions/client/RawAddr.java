package com.cointracker.transactions.client;

import lombok.Data;

import java.util.Collection;

@Data
public class RawAddr {
  private String address;
  private Collection<Tx> txs;
}
