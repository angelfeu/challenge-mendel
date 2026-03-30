package com.prueba.mendel.controller;

import com.prueba.mendel.dto.StatusResponse;
import com.prueba.mendel.dto.SumResponse;
import com.prueba.mendel.dto.TransactionRequest;
import com.prueba.mendel.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Gestión de transacciones vinculadas por relación padre-hijo")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Crear o actualizar una transacción", description = "Registra una transacción con un monto y tipo. Opcionalmente puede vincularse a una transacción padre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacción guardada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o referencia circular detectada"),
            @ApiResponse(responseCode = "404", description = "La transacción padre no existe")
    })
    @PutMapping("/{transactionId}")
    public ResponseEntity<StatusResponse> save(
            @Parameter(description = "Identificador único de la transacción") @PathVariable Long transactionId,
            @Valid @RequestBody TransactionRequest request) {
        log.info("PUT /transactions/{} type={} amount={}", transactionId, request.getType(), request.getAmount());
        transactionService.save(transactionId, request);
        return ResponseEntity.ok(StatusResponse.ok());
    }

    @Operation(summary = "Obtener IDs por tipo", description = "Retorna la lista de IDs de todas las transacciones que corresponden al tipo indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de IDs (puede ser vacía)")
    })
    @GetMapping("/types/{type}")
    public ResponseEntity<List<Long>> findByType(
            @Parameter(description = "Tipo de transacción a consultar") @PathVariable String type) {
        log.info("GET /transactions/types/{}", type);
        return ResponseEntity.ok(transactionService.findIdsByType(type));
    }

    @Operation(summary = "Calcular suma transitiva", description = "Retorna la suma del monto de la transacción indicada más todos sus descendientes vinculados transitivamente por parent_id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suma calculada correctamente"),
            @ApiResponse(responseCode = "404", description = "La transacción no existe")
    })
    @GetMapping("/sum/{transactionId}")
    public ResponseEntity<SumResponse> getSum(
            @Parameter(description = "Identificador de la transacción raíz") @PathVariable Long transactionId) {
        log.info("GET /transactions/sum/{}", transactionId);
        return ResponseEntity.ok(new SumResponse(transactionService.calculateSum(transactionId)));
    }
}
