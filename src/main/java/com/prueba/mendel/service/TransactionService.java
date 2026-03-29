package com.prueba.mendel.service;

import com.prueba.mendel.dto.TransactionRequest;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    void save(Long id, TransactionRequest request);

    List<Long> findIdsByType(String type);

    BigDecimal calculateSum(Long id);
}
