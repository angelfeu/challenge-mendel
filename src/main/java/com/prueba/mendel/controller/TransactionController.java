package com.prueba.mendel.controller;

import com.prueba.mendel.dto.StatusResponse;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PutMapping("/{transactionId}")
    public ResponseEntity<StatusResponse> save(@PathVariable Long transactionId,
                                               @Valid @RequestBody TransactionRequest request) {
        transactionService.save(transactionId, request);
        return ResponseEntity.ok(StatusResponse.ok());
    }
}
