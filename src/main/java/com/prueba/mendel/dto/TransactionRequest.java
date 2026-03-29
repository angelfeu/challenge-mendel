package com.prueba.mendel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransactionRequest {

    @NotBlank(message = "type is required")
    private String type;

    private BigDecimal amount;

    @JsonProperty("parent_id")
    private Long parentId;
}
