package com.prueba.mendel.service;

import com.prueba.mendel.domain.Transaction;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.exception.CircularReferenceException;
import com.prueba.mendel.exception.TransactionNotFoundException;
import com.prueba.mendel.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public void save(Long id, TransactionRequest request) {
        log.debug("Saving transaction id={} type={} amount={} parentId={}", id, request.getType(), request.getAmount(), request.getParentId());
        if (request.getParentId() != null) {
            log.debug("Validating parent exists: parentId={}", request.getParentId());
            transactionRepository.findById(request.getParentId())
                    .orElseThrow(() -> new TransactionNotFoundException(request.getParentId()));
            log.debug("Validating no circular reference: newId={} parentId={}", id, request.getParentId());
            validateNoCircularReference(id, request.getParentId());
        }
        transactionRepository.save(Transaction.builder()
                .id(id)
                .amount(request.getAmount())
                .type(request.getType())
                .parentId(request.getParentId())
                .build());
        log.debug("Transaction saved successfully id={}", id);
    }

    private void validateNoCircularReference(Long newId, Long parentId) {
        Set<Long> visited = new HashSet<>();
        Long current = parentId;
        while (current != null) {
            if (!visited.add(current)) break;
            if (current.equals(newId)) throw new CircularReferenceException(newId);
            current = transactionRepository.findById(current)
                    .map(Transaction::getParentId)
                    .orElse(null);
        }
    }

    @Override
    public List<Long> findIdsByType(String type) {
        log.debug("Finding transactions by type={}", type);
        List<Long> ids = transactionRepository.findByType(type).stream()
                .map(Transaction::getId)
                .toList();
        log.debug("Found {} transactions for type={}", ids.size(), type);
        return ids;
    }

    @Override
    public BigDecimal calculateSum(Long id) {
        log.debug("Calculating transitive sum for transactionId={}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        BigDecimal sum = sumRecursive(transaction);
        log.debug("Transitive sum for transactionId={} result={}", id, sum);
        return sum;
    }

    private BigDecimal sumRecursive(Transaction transaction) {
        return transactionRepository.findByParentId(transaction.getId()).stream()
                .map(this::sumRecursive)
                .reduce(transaction.getAmount(), BigDecimal::add);
    }
}
