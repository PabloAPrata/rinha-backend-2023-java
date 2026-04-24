-- 1. Habilita a extensão para busca por trigramas
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 2. Cria a função de concatenação (Searchable)
CREATE OR REPLACE FUNCTION generate_searchable(_nome VARCHAR, _apelido VARCHAR, _stack JSON)
RETURNS TEXT AS $$
BEGIN
    -- Concatenamos com espaços para evitar junção de palavras e convertemos JSON para texto
RETURN _nome || ' ' || _apelido || ' ' || COALESCE(_stack::text, '');
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- 3. Cria a tabela com a coluna gerada
CREATE TABLE IF NOT EXISTS pessoas (
    id UUID PRIMARY KEY,
    apelido VARCHAR(32) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    nascimento DATE NOT NULL,
    stack JSON,
    searchable TEXT GENERATED ALWAYS AS (generate_searchable(nome, apelido, stack)) STORED
    );

-- 4. Cria os Índices Otimizados
CREATE INDEX IF NOT EXISTS idx_pessoas_searchable
    ON public.pessoas USING gist (searchable public.gist_trgm_ops (siglen=64));

-- Índice B-Tree padrão para buscas exatas por apelido
CREATE UNIQUE INDEX IF NOT EXISTS pessoas_apelido_index
    ON public.pessoas USING btree (apelido);