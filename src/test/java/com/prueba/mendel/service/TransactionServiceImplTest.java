package com.prueba.mendel.service;

import com.prueba.mendel.domain.Transaction;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void save_maps_request_to_transaction_and_persists() {
        TransactionRequest request = new TransactionRequest("cars", BigDecimal.valueOf(5000), null);

        transactionService.save(10L, request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertEquals(10L, saved.getId());
        assertEquals(BigDecimal.valueOf(5000), saved.getAmount());
        assertEquals("cars", saved.getType());
        assertNull(saved.getParentId());
    }

    @Test
    void find_ids_by_type_returns_matching_ids() {
        Transaction t1 = Transaction.builder().id(10L).amount(BigDecimal.valueOf(5000)).type("cars").build();
        Transaction t2 = Transaction.builder().id(11L).amount(BigDecimal.valueOf(3000)).type("cars").build();
        when(transactionRepository.findByType("cars")).thenReturn(List.of(t1, t2));

        List<Long> ids = transactionService.findIdsByType("cars");

        assertEquals(List.of(10L, 11L), ids);
    }

    @Test
    void find_ids_by_type_returns_empty_when_no_match() {
        when(transactionRepository.findByType("unknown")).thenReturn(List.of());

        List<Long> ids = transactionService.findIdsByType("unknown");

        assertTrue(ids.isEmpty());
    }

    @Test
    void save_maps_parent_id_when_present() {
        TransactionRequest request = new TransactionRequest("shopping", BigDecimal.valueOf(10000), 10L);

        transactionService.save(11L, request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertEquals(10L, captor.getValue().getParentId());
    }
}
