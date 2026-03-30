package com.prueba.mendel.integration;

import com.prueba.mendel.controller.TransactionController;
import com.prueba.mendel.controllers.ControllerTest;
import com.prueba.mendel.dto.SumResponse;
import com.prueba.mendel.exception.GlobalExceptionHandler;
import com.prueba.mendel.repository.InMemoryTransactionRepository;
import com.prueba.mendel.service.TransactionServiceImpl;
import com.prueba.mendel.dto.TransactionRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionIntegrationTest extends ControllerTest {

    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    private final TransactionServiceImpl service = new TransactionServiceImpl(repository);
    private final TransactionController controller = new TransactionController(service);

    @Override
    protected Object getTarget() {
        return controller;
    }

    @Override
    protected Object[] getControllerAdvice() {
        return new Object[]{ new GlobalExceptionHandler() };
    }

    @Test
    void pdf_example_scenario() throws Exception {
        // ejemplo en pdf: PUT /transactions/10 { "amount": 5000, "type": "cars" }
        perform(put("/transactions/10"), new TransactionRequest("cars", BigDecimal.valueOf(5000), null), status().isOk());

        // ejemplo en pdf: PUT /transactions/11 { "amount": 10000, "type": "shopping", "parent_id": 10 }
        perform(put("/transactions/11"), new TransactionRequest("shopping", BigDecimal.valueOf(10000), 10L), status().isOk());

        // ejemplo en pdf: PUT /transactions/12 { "amount": 5000, "type": "shopping", "parent_id": 11 }
        perform(put("/transactions/12"), new TransactionRequest("shopping", BigDecimal.valueOf(5000), 11L), status().isOk());

        // ejemplo en pdf: GET /transactions/types/cars => [10]
        List<Long> types = perform(get("/transactions/types/cars"), null, new TypeReference<>() {}, status().isOk());
        assertEquals(List.of(10L), types);

        // ejemplo en pdf: GET /transactions/sum/10 => {"sum": 20000}
        SumResponse sum10 = perform(get("/transactions/sum/10"), null, SumResponse.class, status().isOk());
        assertEquals(BigDecimal.valueOf(20000), sum10.getSum());

        // ejemplo en pdf: GET /transactions/sum/11 => {"sum": 15000}
        SumResponse sum11 = perform(get("/transactions/sum/11"), null, SumResponse.class, status().isOk());
        assertEquals(BigDecimal.valueOf(15000), sum11.getSum());
    }
}
