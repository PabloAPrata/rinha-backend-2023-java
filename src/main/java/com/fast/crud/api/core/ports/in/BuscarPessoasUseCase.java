package com.fast.crud.api.core.ports.in;

import com.fast.crud.api.core.domain.Pessoa;

import java.util.List;

public interface BuscarPessoasUseCase {
    List<Pessoa> searchByTerm(String termo);
}
