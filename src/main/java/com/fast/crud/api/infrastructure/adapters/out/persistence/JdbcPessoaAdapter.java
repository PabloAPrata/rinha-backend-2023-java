package com.fast.crud.api.infrastructure.adapters.out.persistence;

import com.fast.crud.api.core.domain.Pessoa;
import com.fast.crud.api.core.ports.out.PessoaRepositoryPort;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectReader; // Adicionado para otimização
import tools.jackson.databind.json.JsonMapper;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcPessoaAdapter implements PessoaRepositoryPort {

    private static final String SQL_FIND_BY_ID = """
            SELECT id, apelido, nome, nascimento, stack
            FROM pessoas
            WHERE id = ?::uuid
            """;

    private static final String SQL_FIND_BY_TERM = """
            SELECT id, apelido, nome, nascimento, stack
            FROM pessoas
            WHERE searchable ILIKE '%' || ? || '%'
            LIMIT 50
            """;

    private static final String SQL_COUNT = """
            SELECT COUNT(1) FROM pessoas
            """;

    private static final String SQL_INSERT = """
            INSERT INTO pessoas (id, apelido, nome, nascimento, stack)
            VALUES (?::uuid, ?, ?, ?, ?::json)
            """;

    private static final String SQL_EXIST_BY_APELIDO = """
            SELECT 1 FROM pessoas WHERE apelido = ? LIMIT 1
            """;

    private final JdbcTemplate jdbcTemplate;
    private final JsonMapper jsonMapper;
    private final ObjectReader stackReader; // Reutilizado para evitar recriação de TypeReference
    private final RowMapper<Pessoa> pessoaRowMapper;

    public JdbcPessoaAdapter(JdbcTemplate jdbcTemplate, JsonMapper jsonMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.jsonMapper = jsonMapper;
        this.stackReader = jsonMapper.readerFor(new TypeReference<List<String>>() {});
        this.pessoaRowMapper = (rs, rowNum) -> new Pessoa(
                UUID.fromString(rs.getString("id")),
                rs.getString("apelido"),
                rs.getString("nome"),
                rs.getDate("nascimento").toLocalDate(),
                parseStack(rs.getString("stack"))
        );
    }

    @Override
    public Pessoa findDetailedById(UUID uuid) {
        // Uso de query com stream para evitar a exceção EmptyResultDataAccessException
        return jdbcTemplate.query(SQL_FIND_BY_ID, pessoaRowMapper, uuid.toString())
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Pessoa> findByMultipleTerms(String termo) {
        return jdbcTemplate.query(SQL_FIND_BY_TERM, pessoaRowMapper, termo);
    }

    @Override
    public long countAll() {
        Long count = jdbcTemplate.queryForObject(SQL_COUNT, Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public Boolean existByApelido(String apelido) {
        // Correção da lógica de existência para evitar exceções e erro de sintaxe
        List<Integer> results = jdbcTemplate.query(SQL_EXIST_BY_APELIDO, (rs, rowNum) -> 1, apelido);
        return !results.isEmpty();
    }

    @Override
    public Pessoa save(Pessoa pessoa) {

        UUID finalId = (pessoa.id() == null) ? UuidCreator.getTimeOrderedEpoch() : pessoa.id();

        jdbcTemplate.update(SQL_INSERT,
                finalId.toString(),
                pessoa.apelido(),
                pessoa.nome(),
                Date.valueOf(pessoa.nascimento()),
                toJson(pessoa.stack())
        );

        return new Pessoa(
                finalId,
                pessoa.apelido(),
                pessoa.nome(),
                pessoa.nascimento(),
                pessoa.stack()
        );
    }

    private List<String> parseStack(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return stackReader.readValue(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(List<String> stack) {
        if (stack == null) return null;
        try {
            return jsonMapper.writeValueAsString(stack);
        } catch (Exception e) {
            return null;
        }
    }
}