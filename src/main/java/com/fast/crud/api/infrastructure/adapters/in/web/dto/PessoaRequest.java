package com.fast.crud.api.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record PessoaRequest(
        @NotBlank
        @Size(max = 32)
        String apelido,

        @NotBlank
        @Size(max = 100)
        String nome,

        @NotNull
        LocalDate nascimento,

        List<@NotBlank @Size(max = 32) String> stack
) {
}