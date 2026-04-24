package com.fast.crud.api.infrastructure.adapters.out.persistence;

import com.fast.crud.api.core.domain.Pessoa;
import com.fast.crud.api.core.ports.out.PessoaRepositoryPort;
import com.fast.crud.api.infrastructure.adapters.out.persistence.entity.PessoaEntity;
import com.fast.crud.api.infrastructure.adapters.out.persistence.repository.SpringDataPessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaPessoaAdapter implements PessoaRepositoryPort {

    private final SpringDataPessoaRepository repository;

    @Override
    public Pessoa save(Pessoa pessoa) {

        PessoaEntity pessoaEntity = PessoaEntity.fromDomain(pessoa);
        pessoaEntity = repository.save(pessoaEntity);

        return pessoaEntity.toDomain();
    }

    @Override
    public List<Pessoa> findByMultipleTerms(String termo) {
        return repository.findByMultipleTerms(termo).stream()
                .map(PessoaEntity::toDomain)
                .toList();
    }

    @Override
    public Pessoa findDetailedById(UUID uuid) {
        return repository.findById(uuid)
                .map(PessoaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public Boolean existByApelido(String apelido) {
        return repository.existsByApelido(apelido);
    }
}
