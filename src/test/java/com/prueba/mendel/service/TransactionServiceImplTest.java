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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

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
    void save_maps_parent_id_when_present() {
        TransactionRequest request = new TransactionRequest("shopping", BigDecimal.valueOf(10000), 10L);

        transactionService.save(11L, request);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertEquals(10L, captor.getValue().getParentId());
    }
}
