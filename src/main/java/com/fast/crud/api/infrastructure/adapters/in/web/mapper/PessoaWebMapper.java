package com.fast.crud.api.infrastructure.adapters.in.web.mapper;

import com.fast.crud.api.core.domain.Pessoa;
import com.fast.crud.api.infrastructure.adapters.in.web.dto.PessoaRequest;
import com.fast.crud.api.infrastructure.adapters.out.web.PessoaResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PessoaWebMapper {

    public Pessoa toDomain(PessoaRequest request) {
        return new Pessoa(
                null,
                request.apelido(),
                request.nome(),
                request.nascimento(),
                request.stack() == null ? Collections.emptyList() : request.stack()
        );
    }

    public PessoaResponse toResponse(Pessoa domain) {
        return new PessoaResponse(
                domain.id(),
                domain.apelido(),
                domain.nome(),
                domain.nascimento(),
                domain.stack() == null ? Collections.emptyList() : domain.stack()
        );
    }
}