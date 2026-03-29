package com.prueba.mendel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusResponse {

    private final String status;

    public static StatusResponse ok() {
        return new StatusResponse("ok");
    }
}
