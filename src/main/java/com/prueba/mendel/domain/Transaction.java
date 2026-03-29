package com.prueba.mendel.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class Transaction {

    private final Long id;
    private final BigDecimal amount;
    private final String type;

    @With
    private final Long parentId;
}
