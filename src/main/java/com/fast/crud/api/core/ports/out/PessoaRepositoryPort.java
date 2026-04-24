package com.fast.crud.api.core.ports.out;

import com.fast.crud.api.core.domain.Pessoa;

import java.util.List;
import java.util.UUID;

public interface PessoaRepositoryPort {
    Pessoa save(Pessoa pessoa);
    List<Pessoa> findByMultipleTerms(String termo);
    Pessoa findDetailedById(UUID uuid);
    long countAll();
    Boolean existByApelido(String apelido);
}
