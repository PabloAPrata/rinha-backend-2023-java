package com.fast.crud.api.infrastructure.adapters.out.web;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PessoaResponse(
        UUID id,
        String apelido,
        String nome,
        LocalDate nascimento,
        List<String> stack
) {
}
