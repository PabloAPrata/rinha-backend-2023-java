package com.fast.crud.api.infrastructure.adapters.in.web;

import com.fast.crud.api.core.ports.in.ContagemPessoasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contagem-pessoas")
public class ContagemPessoasController {

    private final ContagemPessoasUseCase contagemPessoasUseCase;

    @GetMapping
    public ResponseEntity<String> contar() {
        long total = contagemPessoasUseCase.execute();
        return ResponseEntity.ok(String.valueOf(total));
    }
}