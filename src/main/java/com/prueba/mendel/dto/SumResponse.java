package com.prueba.mendel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class SumResponse {

    private final BigDecimal sum;
}
