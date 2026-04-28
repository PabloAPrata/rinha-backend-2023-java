package com.fast.crud.api.infrastructure.adapters.in.web;

import com.fast.crud.api.core.ports.in.BuscarPessoasUseCase;
import com.fast.crud.api.core.ports.in.CreatePessoaUseCase;
import com.fast.crud.api.core.ports.in.DetalharPessoaUseCase;
import com.fast.crud.api.infrastructure.adapters.in.web.dto.PessoaRequest;
import com.fast.crud.api.infrastructure.adapters.in.web.mapper.PessoaWebMapper;
import com.fast.crud.api.infrastructure.adapters.out.web.PessoaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pessoas")
public class PessoaController {

    private final CreatePessoaUseCase createPessoaUseCase;
    private final DetalharPessoaUseCase detalharPessoaUseCase;
    private final BuscarPessoasUseCase buscarPessoasUseCase;
    private final PessoaWebMapper mapper;

    @PostMapping
    public ResponseEntity<Void> cadastrarPessoa(@RequestBody @Valid PessoaRequest request) {
        var response = createPessoaUseCase.createPessoa(mapper.toDomain(request));

        URI uri = URI.create("/pessoas/" + response.id());

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponse> detalharPessoa(@PathVariable UUID id) {
        var response = mapper.toResponse(detalharPessoaUseCase.searchById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponse>> buscarTermoPessoas(@RequestParam(name = "t", required = false) String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var resultados = buscarPessoasUseCase.searchByTerm(termo);

        var response = resultados.stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
