package com.fast.crud.api.infrastructure.adapters.out.persistence.repository;

import com.fast.crud.api.infrastructure.adapters.out.persistence.entity.PessoaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataPessoaRepository extends JpaRepository<PessoaEntity, UUID> {

	boolean existsByApelido(String apelido);

	@Query(value = """
        SELECT * FROM pessoas WHERE searchable ILIKE %:termo% LIMIT 50""", nativeQuery = true)
	List<PessoaEntity> findByMultipleTerms(@Param("termo") String termo);

	@Query(value = "SELECT * FROM pessoas p WHERE p.searchable % :termo ORDER BY similarity(p.searchable, :termo) DESC", nativeQuery = true)
	List<PessoaEntity> searchFuzzy(@Param("termo") String termo);
}
