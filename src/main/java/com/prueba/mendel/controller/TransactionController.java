package com.prueba.mendel.controller;

import com.prueba.mendel.dto.StatusResponse;
import com.prueba.mendel.dto.SumResponse;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/types/{type}")
    public ResponseEntity<List<Long>> findByType(@PathVariable String type) {
        return ResponseEntity.ok(transactionService.findIdsByType(type));
    }

    @GetMapping("/sum/{transactionId}")
    public ResponseEntity<SumResponse> getSum(@PathVariable Long transactionId) {
        return ResponseEntity.ok(new SumResponse(transactionService.calculateSum(transactionId)));
    }
}
