package com.prueba.mendel.service;

import com.prueba.mendel.dto.TransactionRequest;

public interface TransactionService {

    void save(Long id, TransactionRequest request);
}
