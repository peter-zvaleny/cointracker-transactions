package com.cointracker.transactions.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("bitcoin_transactions")
@CompoundIndexes({
    @CompoundIndex(name = "bt_tx_address_time", def = "{'address' : 1, 'time': -1}")
})
public class Transaction {

  private String address;
  @Indexed(unique = true)
  private String hash;
  private Long time;
  private Long result;
  private Long balance;

}
