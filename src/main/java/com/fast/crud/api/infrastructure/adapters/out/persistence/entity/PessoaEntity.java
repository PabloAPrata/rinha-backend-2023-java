package com.fast.crud.api.infrastructure.adapters.out.persistence.entity;

import com.fast.crud.api.core.domain.Pessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pessoas")
public class PessoaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String apelido;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 10)
    private LocalDate nascimento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> stack;

    @Column(name = "searchable", insertable = false, updatable = false)
    private String searchable;

    public static PessoaEntity fromDomain(Pessoa pessoa) {
        PessoaEntity entity = new PessoaEntity();
        entity.setApelido(pessoa.apelido());
        entity.setNome(pessoa.nome());
        entity.setNascimento(pessoa.nascimento());
        entity.setStack(pessoa.stack());
        return entity;
    }

    public Pessoa toDomain() {
        return new Pessoa(
                this.id,
                this.apelido,
                this.nome,
                this.nascimento,
                this.stack
        );
    }
}
