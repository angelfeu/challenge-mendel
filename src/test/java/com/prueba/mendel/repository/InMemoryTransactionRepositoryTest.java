package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void save_stores_transaction() {
        Transaction transaction = Transaction.builder()
                .id(10L)
                .amount(BigDecimal.valueOf(5000))
                .type("cars")
                .parentId(null)
                .build();

        repository.save(transaction);

        assertEquals(1, repository.count());
    }

    @Test
    void save_overwrites_existing_transaction_with_same_id() {
        Transaction first = Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build();
        Transaction second = Transaction.builder().id(10L).amount(BigDecimal.valueOf(9000)).type("cars").build();

        repository.save(first);
        repository.save(second);

        assertEquals(1, repository.count());
    }
}
