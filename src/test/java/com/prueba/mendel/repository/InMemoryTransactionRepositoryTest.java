package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void find_by_type_returns_matching_transactions() {
        repository.save(Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build());
        repository.save(Transaction.builder().id(11L).amount(BigDecimal.valueOf(3000)).type("cars").build());
        repository.save(Transaction.builder().id(12L).amount(BigDecimal.valueOf(1000)).type("shopping").build());

        List<Transaction> result = repository.findByType("cars");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getType().equals("cars")));
    }

    @Test
    void find_by_type_returns_empty_when_no_match() {
        repository.save(Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build());

        List<Transaction> result = repository.findByType("unknown");

        assertTrue(result.isEmpty());
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
