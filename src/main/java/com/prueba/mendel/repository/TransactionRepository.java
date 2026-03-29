package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;

import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    List<Transaction> findByType(String type);
}
