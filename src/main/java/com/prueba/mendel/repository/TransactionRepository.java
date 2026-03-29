package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    void save(Transaction transaction);

    List<Transaction> findByType(String type);

    Optional<Transaction> findById(Long id);

    List<Transaction> findByParentId(Long parentId);
}
