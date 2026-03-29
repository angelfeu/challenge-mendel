package com.prueba.mendel.repository;

import com.prueba.mendel.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void find_by_id_returns_transaction_when_exists() {
        Transaction transaction = Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build();
        repository.save(transaction);

        Optional<Transaction> result = repository.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void find_by_id_returns_empty_when_not_found() {
        Optional<Transaction> result = repository.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void find_by_parent_id_returns_direct_children() {
        repository.save(Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build());
        repository.save(Transaction.builder().id(11L).amount(BigDecimal.valueOf(10000)).type("shopping").parentId(10L).build());
        repository.save(Transaction.builder().id(12L).amount(BigDecimal.valueOf(5000)).type("shopping").parentId(10L).build());

        List<Transaction> children = repository.findByParentId(10L);

        assertEquals(2, children.size());
        assertTrue(children.stream().allMatch(t -> Long.valueOf(10L).equals(t.getParentId())));
    }

    @Test
    void find_by_parent_id_returns_empty_when_no_children() {
        repository.save(Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build());

        List<Transaction> children = repository.findByParentId(10L);

        assertTrue(children.isEmpty());
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
