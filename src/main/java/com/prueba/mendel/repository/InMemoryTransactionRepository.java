package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final ConcurrentHashMap<Long, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public void save(Transaction transaction) {
        store.put(transaction.getId(), transaction);
    }

    public int count() {
        return store.size();
    }
}
