package com.fast.crud.api.core.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Pessoa(
        UUID id,
        String apelido,
        String nome,
        LocalDate nascimento,
        List<String> stack
) {
    public Pessoa {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
