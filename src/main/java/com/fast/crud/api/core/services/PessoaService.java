package com.fast.crud.api.core.services;

import com.fast.crud.api.core.domain.Pessoa;
import com.fast.crud.api.core.ports.in.BuscarPessoasUseCase;
import com.fast.crud.api.core.ports.in.ContagemPessoasUseCase;
import com.fast.crud.api.core.ports.in.CreatePessoaUseCase;
import com.fast.crud.api.core.ports.in.DetalharPessoaUseCase;
import com.fast.crud.api.core.ports.out.PessoaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PessoaService implements CreatePessoaUseCase, DetalharPessoaUseCase, BuscarPessoasUseCase, ContagemPessoasUseCase {

    private final PessoaRepositoryPort repository;

    public PessoaService(PessoaRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Pessoa createPessoa(Pessoa pessoa) {

        if (repository.existByApelido(pessoa.apelido())) {
            throw new IllegalArgumentException();
        }

        return repository.save(pessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pessoa> searchByTerm(String termo) {
        return repository.findByMultipleTerms(termo);
    }

    @Override
    @Transactional(readOnly = true)
    public Pessoa searchById(UUID uuid) {
        Pessoa pessoa = repository.findDetailedById(uuid);

        if (pessoa == null) {
            throw new NoSuchElementException("ID não encontrado");
        }
        return pessoa;
    }

    @Override
    @Transactional(readOnly = true)
    public long execute() {
        return repository.countAll();
    }
}