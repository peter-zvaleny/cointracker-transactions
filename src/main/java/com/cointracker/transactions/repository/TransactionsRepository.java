package com.cointracker.transactions.repository;

import com.cointracker.transactions.model.Transaction;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class TransactionsRepository {

  private final MongoTemplate mongoTemplate;

  public TransactionsRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public List<Transaction> findTransactions(String address) {
    // todo pagination
    var query = new Query(Criteria.where("address").is(address)).limit(50);
    return mongoTemplate.find(query, Transaction.class);
  }

  public boolean insertTransactions(Collection<Transaction> transactions) {
    try {
      mongoTemplate.insertAll(transactions);
    } catch (DuplicateKeyException ex) {
      // transactions already synced to db
      return false;
    }
    return true;
  }

}
