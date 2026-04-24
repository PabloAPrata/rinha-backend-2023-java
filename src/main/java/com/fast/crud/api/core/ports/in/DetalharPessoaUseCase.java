package com.fast.crud.api.core.ports.in;

import com.fast.crud.api.core.domain.Pessoa;

import java.util.UUID;

public interface DetalharPessoaUseCase {
    Pessoa searchById(UUID uuid);
}
