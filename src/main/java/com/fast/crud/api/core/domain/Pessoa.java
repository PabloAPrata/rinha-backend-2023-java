package com.fast.crud.api.core.domain;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record Pessoa(
        UUID id,
        String apelido,
        String nome,
        LocalDate nascimento,
        List<String> stack
) {
}
