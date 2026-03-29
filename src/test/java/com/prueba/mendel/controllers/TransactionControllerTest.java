package com.prueba.mendel.controllers;

import com.prueba.mendel.controller.TransactionController;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.dto.StatusResponse;
import com.prueba.mendel.exception.GlobalExceptionHandler;
import com.prueba.mendel.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest extends ControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Override
    protected Object getTarget() {
        return transactionController;
    }

    @Override
    protected Object[] getControllerAdvice() {
        return new Object[]{ new GlobalExceptionHandler() };
    }

    @Test
    void put_transaction_returns_ok() throws Exception {
        TransactionRequest request = new TransactionRequest("cars", BigDecimal.valueOf(5000), null);

        StatusResponse response = perform(put("/transactions/10"), request, StatusResponse.class, status().isOk());

        assertEquals("ok", response.getStatus());
        verify(transactionService).save(10L, request);
    }

    @Test
    void put_transaction_with_parent_returns_ok() throws Exception {
        TransactionRequest request = new TransactionRequest("shopping", BigDecimal.valueOf(10000), 10L);

        StatusResponse response = perform(put("/transactions/11"), request, StatusResponse.class, status().isOk());

        assertEquals("ok", response.getStatus());
        verify(transactionService).save(11L, request);
    }

    @Test
    void put_transaction_returns_400_when_type_is_blank() throws Exception {
        TransactionRequest request = new TransactionRequest("", BigDecimal.valueOf(5000), null);

        perform(put("/transactions/10"), request, status().isBadRequest());
    }
}
