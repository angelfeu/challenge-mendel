package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final ConcurrentHashMap<Long, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public void save(Transaction transaction) {
        store.put(transaction.getId(), transaction);
    }

    @Override
    public List<Transaction> findByType(String type) {
        return store.values().stream()
                .filter(t -> t.getType().equals(type))
                .toList();
    }

    public int count() {
        return store.size();
    }
}
