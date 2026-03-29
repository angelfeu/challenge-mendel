package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;

public interface TransactionRepository {

    void save(Transaction transaction);
}
