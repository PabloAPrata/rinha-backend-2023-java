# Rinha Backend 2023 — Resumo do projeto

**API preparada para a competição "Rinha de Backend" 2023; serviço CRUD para cadastro, busca por termo e contagem de registros de pessoas.**   
**Esta API oferece operações básicas para gerenciar recursos do tipo Pessoa: criação, recuperação por id, busca por termo e contagem total de registros.**

---

## Principais funcionalidades

#### Casos de uso
- **CreatePessoa** — cadastro de pessoa com validação de *apelido* único e restrições de tamanho.
- **DetalharPessoa** — recuperação de pessoa por UUID.
- **BuscarPessoas** — busca case‑insensitive em `nome`, `apelido` e elementos de `stack`; resultado limitado a **50** registros.
- **ContagemPessoas** — retorna o número total de registros como texto puro.

#### Clientes e integração
- Ferramenta de teste/benchmark: **Gatling**.
- Load balancer: **Nginx** (configurado no `docker-compose` para balancear duas instâncias).
- Consumidores típicos: front‑ends, scripts de integração ou outras APIs.

---

## Endpoints da API

#### Visão geral
- **Base path:** `/`
- **Versão da API:** `1.0.0` (extraído do `pom.xml`)

#### Tabela de endpoints

| Método | Endpoint | Operation ID | Descrição |
|---:|---|---|---|
| POST | `/pessoas` | createPessoa | Cria nova pessoa. Requisição: JSON `PessoaRequest`. Retorna **201** e header `Location: /pessoas/{id}` |
| GET | `/pessoas/{id}` | detalharPessoa | Retorna dados de uma pessoa por UUID |
| GET | `/pessoas?t={termo}` | buscarTermoPessoas | Busca por termo em `nome`, `apelido`, `stack`. Parâmetro `t` obrigatório; retorna lista (máx 50) |
| GET | `/contagem-pessoas` | contar | Retorna número total de registros como texto puro |

#### Headers obrigatórios
| Header | Descrição |
|---|---|
| `Content-Type` | Para `POST /pessoas`: `application/json` |
| `Accept` | Recomenda‑se `application/json` para endpoints que retornam JSON |

#### Parâmetros de query
| Endpoint | Parâmetro | Tipo | Obrigatório | Descrição |
|---|---:|---|---:|---|
| `GET /pessoas` | `t` | string | sim | Termo de busca (procurado em `nome`, `apelido`, `stack`) |

#### Códigos de resposta principais
| Código | Significado |
|---:|---|
| 201 | Created (Location header) — `POST /pessoas` quando válido |
| 200 | OK — `GET /pessoas/{id}`, `GET /pessoas?t=...`, `GET /contagem-pessoas` |
| 400 | Bad Request — JSON inválido, parâmetro `t` faltando, conversão de tipos |
| 404 | Not Found — recurso não encontrado |
| 422 | Unprocessable Content — validação falhou, apelido duplicado, violação de integridade |
| 500 | Erro genérico (tratado como 400 pelo `GlobalExceptionHandler` em alguns casos) |

---

## Modelos de dados

#### Principais DTOs / Domain
- **PessoaRequest**
    - `apelido`: string, **máx 32**, not blank
    - `nome`: string, **máx 100**, not blank
    - `nascimento`: `LocalDate` (YYYY‑MM‑DD), not null
    - `stack`: `List<string>` ou `null` (cada elemento not blank, máx 32)

- **PessoaResponse**
    - `id`: UUID
    - `apelido`, `nome`, `nascimento`, `stack`: `List<string>`

- **Pessoa (domain)** — record interno: `id`, `apelido`, `nome`, `nascimento`, `stack`.

#### Observações sobre `stack`
- Na criação, o `PessoaWebMapper` converte `null` → `emptyList`; portanto a resposta **nunca** terá `stack: null` por padrão. Ajuste o mapper se desejar preservar `null`.

---

## Estrutura do projeto

#### Arquivos e diretórios principais

```text
rinha-backend-2023-java/                      # raiz do projeto (D:\Code\Projetos\crud-java-test)
|-- Dockerfile                                # multi-stage build, JDK 25
|-- docker-compose.yml                        # definicao de app1, app2, nginx, postgres
|-- pom.xml                                   # dependencias e parent Spring Boot 4.0.5
|-- INSTRUCOES.md                             # instrucao do torneio (Rinha de Backend)
|-- nginx.conf                                # configuracao do load balancer
|-- src/
|   `-- main/
|       |-- java/
|       |   `-- com/fast/crud/api/
|       |       |-- FastCrudApiApplication.java    # SpringBootApplication
|       |       |-- core/
|       |       |   |-- domain/
|       |       |   |   `-- Pessoa.java            # record dominio
|       |       |   |-- ports/
|       |       |   |   |-- in/                    # use-case interfaces
|       |       |   `-- out/                       # repository port
|       |       |   `-- services/
|       |       |       `-- PessoaService.java     # implementacao dos use-cases
|       |       |
|       |       `-- infrastructure/
|       |           |-- adapters/
|       |           |   |-- in/
|       |           |   |   `-- web/
|       |           |   |       |-- PessoaController.java
|       |           |   |       |-- ContagemPessoasController.java
|       |           |   |       |-- dto/PessoaRequest.java
|       |           |   |       |-- mapper/PessoaWebMapper.java
|       |           |   |       `-- exception/GlobalExceptionHandler.java
|       |           |   `-- out/
|       |           |       |-- persistence/
|       |           |       |   |-- entity/PessoaEntity.java
|       |           |       |   |-- repository/SpringDataPessoaRepository.java
|       |           |       |   `-- JpaPessoaAdapter.java
|       |           |       `-- web/PessoaResponse.java
|       |           `-- config/
|       |               `-- JacksonCoercionConfig.java
|       `-- resources/
|           |-- application.yaml                 # configuracoes spring, datasource, flyway
|           `-- db/
|               `-- migration/
|                   `-- V1__init.sql             # cria tabela pessoas, funcao generate_searchable
|-- src/test/
|   `-- java/.../FastCrudApiApplicationTests.java # Spring context smoke test


---



#### Logs e monitoramento
- Logs: `application.yaml` define níveis como `OFF` para muitos pacotes; não há agente externo configurado.


#### Deploy
- `docker-compose.yml` pronto para execução local com 2 instâncias e Nginx.

---

## Banco, servidor e JVM

#### Banco de dados
- **PostgreSQL** (driver `org.postgresql:postgresql 42.7.10`)
- Migrações com **Flyway** (`V1__init.sql` cria tabela `pessoas` e função `generate_searchable`)
- HikariCP: `SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE` padrão **40**

#### Servidor
- Tomcat configurado em `application.yaml`:
    - `accept-count: 2000`
    - `max-connections: 20000`
    - `keep-alive-timeout: 2000`
    - `max-keep-alive-requests: 1000`
    - `threads.max: 20`

#### JVM options (Dockerfile)
```text
java -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -jar app.jar
```
---

## Tratamento de erros e suporte

#### Mapeamento de exceções (GlobalExceptionHandler)
| Código HTTP | Exceções mapeadas | Descrição |
|---:|---|---|
| 422 | `MethodArgumentNotValidException`, `DataIntegrityViolationException`, `IllegalArgumentException` | Validação ou violação de integridade (ex.: apelido duplicado) |
| 404 | `NoSuchElementException`, `NoResourceFoundException` | Recurso não encontrado |
| 400 | `HttpMessageNotReadableException`, `MissingServletRequestParameterException`, `MethodArgumentTypeMismatchException` | Bad request (JSON inválido, parâmetro faltando) |
| 400 | `Exception` (fallback) | Exceção genérica (imprime stacktrace e retorna 400) |

- `PessoaService.createPessoa` lança `IllegalArgumentException` quando apelido já existe (mapeado para 422).
- Violação de constraints do banco gera `DataIntegrityViolationException` (mapeado para 422).

#### Suporte e referências
- Pontos de interesse no código:
    - `src/main/java/com/fast/crud/api/FastCrudApiApplication.java`
    - `src/main/java/com/fast/crud/api/core/domain/Pessoa.java`
    - `src/main/java/com/fast/crud/api/infrastructure/adapters/in/web/PessoaController.java`
    - `src/main/java/com/fast/crud/api/infrastructure/adapters/out/persistence/PessoaEntity.java`
    - `src/main/resources/db/migration/V1__init.sql`
    - `Dockerfile`, `docker-compose.yml`, `INSTRUCOES.md`

---
