package com.prueba.mendel.service;

import com.prueba.mendel.domain.Transaction;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void save(Long id, TransactionRequest request) {
        transactionRepository.save(Transaction.builder()
                .id(id)
                .amount(request.getAmount())
                .type(request.getType())
                .parentId(request.getParentId())
                .build());
    }

    @Override
    public List<Long> findIdsByType(String type) {
        return transactionRepository.findByType(type).stream()
                .map(Transaction::getId)
                .toList();
    }

    @Override
    public BigDecimal calculateSum(Long id) {
        throw new UnsupportedOperationException();
    }
}
