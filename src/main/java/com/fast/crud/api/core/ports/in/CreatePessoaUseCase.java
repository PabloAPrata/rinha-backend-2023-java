package com.fast.crud.api.core.ports.in;

import com.fast.crud.api.core.domain.Pessoa;

public interface CreatePessoaUseCase {
    Pessoa createPessoa(Pessoa pessoa);
}
