# 🚗 EasyPark

API REST para gerenciamento de estacionamentos, vagas e estadias de veículos, desenvolvida em Java com Spring Boot — evolução de um sistema originalmente feito em Portugol.

> ⚠️ **Projeto em fase de estruturação e aprendizado.**
> Este é um projeto de estudos focado em Spring Boot, JPA e arquitetura em camadas. As decisões aqui documentadas refletem o processo de aprendizado na prática. A camada de persistência está concluída e o **módulo de Carro já funciona de ponta a ponta** (Controller → Service → Repository). Os módulos de Estacionamento, Vaga e Estadia ainda não possuem Service nem Controller, portanto o sistema **ainda não está completo**.

---

## 📖 Sobre o projeto

O **EasyPark** gerencia múltiplos estacionamentos (pátios), cada um com vagas comuns, de idoso e PCD. O sistema foi projetado para:

- Cadastrar estacionamentos informando a quantidade de vagas por categoria (comuns, idoso e PCD).
- Registrar a entrada e saída de veículos automaticamente com horário gerado pelo servidor (`LocalDateTime.now()`).
- Impedir estacionamento em vagas reservadas (idoso/PCD) para veículos não elegíveis.
- Impedir que a mesma placa esteja estacionada em duas estadias ativas simultaneamente.
- Impedir que a mesma vaga receba duas estadias ativas simultaneamente.
- Calcular o valor cobrado com base no tempo de permanência no momento da saída.
- Consultar vagas livres/ocupadas, veículos atualmente estacionados e histórico de estadias.

---

## 🛠️ Tecnologias e Stack

| Tecnologia | Função / Uso |
|---|---|
| **Java 21** | Linguagem de programação (`java.version` do `pom.xml`) |
| **Spring Boot 4.1.1** | Framework base da aplicação |
| **Spring Web MVC** | Camada REST (`spring-boot-starter-webmvc`) |
| **Spring Data JPA** | Abstração de persistência de dados |
| **Hibernate 7** | ORM / Provedor JPA |
| **PostgreSQL** | Banco de dados relacional |
| **Lombok** | Redução de código boilerplate (`@Getter`, `@Setter`, `@RequiredArgsConstructor`, etc.) |
| **Maven** | Gerenciador de dependências e build |

---

## 📊 Status geral do projeto

| Camada / Componente | Status | Detalhes |
|---|---|---|
| **Modelagem (Entidades)** | ✅ Concluída | `Carro`, `Estacionamento`, `Vaga`, `Estadia` criadas |
| **JPA (Anotações / Relacionamentos)** | ✅ Concluída | `@OneToMany`, `@ManyToOne`, `@SequenceGenerator` aplicados |
| **Banco de Dados (PostgreSQL)** | ✅ Conectado | Tabelas criadas e atualizadas automaticamente via Hibernate |
| **Repositories** | ✅ Concluída (4/4) | Spring Data JPA interfaces com Query Methods criadas |
| **Dependência Web (Spring MVC)** | ✅ Concluída | `spring-boot-starter-webmvc` e starter de teste no `pom.xml` |
| **DTOs** | 🟡 Em andamento | `Carro` concluído (`CarroRequestDTO`, `CarroDTO`); demais entidades pendentes |
| **Service (Regras de Negócio)** | 🟡 Em andamento | `CarroService` concluído; `EstacionamentoService` e `EstadiaService` pendentes |
| **Controller (Endpoints REST)** | 🟡 Em andamento | `CarroController` com `GET` e `POST`; demais controllers pendentes |
| **Tratamento Global de Exceções** | 🟡 Parcial | `@RestControllerAdvice` com 4 handlers (404, 409, 400 e 409 de integridade) |
| **Validações (Bean Validation)** | ⬜ Não iniciada | Anotações `@NotBlank`, `@Pattern`, `@Size` |
| **Testes Unitários / Integração** | ⬜ Não iniciada | Apenas o `contextLoads` gerado pelo Spring Initializr existe |

---

## 🏗️ Arquitetura (MVC em Camadas)

```
com.br.lucasthest8ic.easypark/
├── controller/      🟡 CarroController
├── service/         🟡 CarroService
├── repository/      ✅ CarroRepository, EstacionamentoRepository, VagaRepository, EstadiaRepository
├── model/           ✅ Carro, Estacionamento, Vaga, Estadia
├── enums/           ✅ TipoVaga
├── dtos/
│   └── carrodto/    ✅ CarroRequestDTO (entrada), CarroDTO (resposta)
└── exception/
    ├── ApiError                  ✅ corpo padrão das respostas de erro
    ├── GlobalExceptionHandler    ✅ @RestControllerAdvice
    └── exceptions/               ✅ ResourceNotFoundException, ResourceAlreadyExistsException, CantBeNullOrBlankException
```

**Responsabilidade de cada camada**

- **Controller:** recebe a requisição, delega ao Service e devolve a resposta. Não contém regra de negócio, não usa `try/catch` e nunca expõe entidades.
- **Service:** guarda as regras de negócio, controla as transações (`@Transactional`) e lança exceções de negócio. Converte entidade em DTO.
- **Repository:** acesso a dados, via Query Methods do Spring Data.
- **Exception handler:** traduz as exceções do Service em respostas HTTP padronizadas.

---

## 🌐 API (endpoints implementados)

Prefixo de todas as rotas: `/easypark` (definido no `@RequestMapping` do controller).

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/easypark/carros/{placa}` | Busca um carro pela placa | `200 OK` |
| `POST` | `/easypark/carros` | Cadastra um carro | `201 Created` |

### Cadastro de carro

`POST /easypark/carros` com o cabeçalho `Content-Type: application/json`:

```json
{
  "placa": "abc1234",
  "modelo": "Gol",
  "cor": "Prata",
  "elegivelVagaIdoso": false
}
```

Resposta (`201 Created`). A placa é normalizada (sem espaços nas pontas e em maiúsculas):

```json
{
  "placa": "ABC1234",
  "modelo": "Gol",
  "cor": "Prata",
  "elegivelVagaIdoso": false
}
```

### Respostas de erro

Todos os erros seguem o mesmo formato (`ApiError`):

```json
{
  "status": 404,
  "message": "Não foi possível encontrar o carro com a placa ZZZ9999",
  "timestamp": "2026-10-21T14:30:00.000"
}
```

| Exceção | Quando ocorre | Status HTTP |
|---|---|---|
| `ResourceNotFoundException` | Carro não encontrado pela placa | `404 Not Found` |
| `ResourceAlreadyExistsException` | Cadastro de placa que já existe | `409 Conflict` |
| `CantBeNullOrBlankException` | Placa nula, vazia ou só com espaços | `400 Bad Request` |
| `DataIntegrityViolationException` | Violação de unicidade no banco (ex.: duas requisições simultâneas com a mesma placa nova) | `409 Conflict` |

> A mensagem do `DataIntegrityViolationException` é fixa de propósito: o texto original traz detalhes técnicos do banco (SQL, nome da constraint) que não devem ir para o cliente.

### Roteiro de testes manuais (Postman / Insomnia)

- [ ] `POST /easypark/carros` com placa `abc1234` → `201`, placa devolvida como `ABC1234`
- [ ] O mesmo `POST` novamente → `409`
- [ ] `POST` com `" abc1234 "` (espaços) → `409`
- [ ] `POST` com `"placa": ""` → `400`
- [ ] `POST` sem o campo `placa` → `400`
- [ ] `GET /easypark/carros/abc1234` → `200`
- [ ] `GET /easypark/carros/ZZZ9999` → `404`
- [ ] `POST` com JSON malformado → `400` (tratado pelo próprio Spring)
- [ ] Conferir no pgAdmin se `modelo` e `cor` caíram nas colunas corretas

---

## 🧠 Decisões de modelagem e por quê

A modelagem do domínio foi construída questionando continuamente se cada informação era um **dado de origem** (precisa ser armazenado na tabela) ou **dado derivado** (pode ser calculado a partir de outros dados já existentes):

### 1. Entidades
- **`Carro` — Reaproveitamento pela placa:** O veículo não é duplicado a cada nova parada. Ao chegar uma placa já existente, busca-se o registro base (`unique = true`). A mesma placa nunca gera um novo registro de `Carro`, apenas uma nova `Estadia`.
- **`Estacionamento` — Pátio enxuto:** Não guarda `totalVagas` nem `totalVagasIdoso` como colunas no banco — ambos são deriváveis da contagem da lista de `vagas` associadas.
- **`Vaga` — Sem redundâncias:** Não existe campo booleano `vagaLivre` ou `carroAtual`. O estado "livre/ocupada" é deduzido pela existência (ou ausência) de uma `Estadia` ativa ligada àquela vaga.
- **`Estadia` — Tabela de associação e histórico:** É a entidade central das regras de negócio. Relaciona `Carro` e `Vaga`, guardando `horarioEntrada`, `horarioSaida` e `valor`. O estado de uma estadia ser **ativa** é definido puramente por `horarioSaida == null`.

### 2. IDs e Estratégias de Persistência
- **Estratégia `GenerationType.SEQUENCE`:** Escolha consciente com `@SequenceGenerator` explícito em todas as entidades. Apresenta performance superior e suporte nativo a *batch inserts* no PostgreSQL (essencial ao criar um pátio com dezenas de vagas de uma só vez).
- **Tipos numéricos baseados em volume:**
  - `Carro`, `Vaga`, `Estacionamento` → `Integer` (crescimento lento / dados cadastrais).
  - `Estadia` → `Long` (cresce continuamente a cada entrada, para sempre).
- **`allocationSize = 1`:** Adotado temporariamente em todas as sequências por simplicidade inicial durante o aprendizado.

### 3. Regras de Tempo e Validações
- **Horário gerado pelo Servidor:** `horarioEntrada` utiliza `LocalDateTime.now()` automático no backend, impedindo que o cliente envie datas arbitrárias e dispensando validações complexas de passado/futuro.
- **Validação de saída:** Não será permitido um `horarioSaida` anterior ao `horarioEntrada`.
- **`TipoVaga` como Enum:** Enum simples (`COMUM`, `IDOSO`, `PCD`), persistido no banco como `EnumType.STRING`.

### 4. Camada de Serviço e API
- **DTOs como `record`:** Entrada (`CarroRequestDTO`) e saída (`CarroDTO`) são separados e imutáveis. O Service devolve DTO, nunca a entidade, o que evita expor o modelo de persistência e o `LazyInitializationException`.
- **Normalização da placa em um único lugar:** O método privado `normalizePlaca` do `CarroService` rejeita placa nula ou em branco e aplica `trim` + `toUpperCase`. Todo método público passa por ele, para que `abc1234` e `ABC1234` nunca virem dois carros.
- **Conversão entidade → DTO centralizada:** O método privado `toDTO` é o único lugar que conhece a ordem dos componentes do `CarroDTO`.
- **Transações:** leitura com `@Transactional(readOnly = true)`, escrita com `@Transactional`.
- **Exceções de negócio com significado único:** cada exceção representa um tipo de erro e o handler global a mapeia para um status HTTP, sem duplicar essa lógica nos controllers.
- **Verificação de duplicidade com `existsByPlaca`:** evita carregar a entidade inteira só para saber se ela existe. A garantia final contra concorrência é o `unique = true` da coluna.

---

## 🗄️ Detalhamento do Modelo de Dados

### `Carro` (`carros`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idCarro` | `Integer` | `@Id`, Sequence |
| `placa` | `String` | `@Column(unique = true, nullable = false, length = 10)` |
| `modelo` | `String` | `@Column(length = 50)` |
| `cor` | `String` | `@Column(length = 30)` |
| `elegivelVagaIdoso` | `boolean` | `nullable = false`; dado de origem (não derivável) |

### `Estacionamento` (`estacionamentos`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idEstacionamento` | `Integer` | `@Id`, Sequence |
| `nome` | `String` | `@Column(name = "nome_estacionamento", unique = true, nullable = false)` (busca case-insensitive no repo) |
| `vagas` | `List<Vaga>` | `@OneToMany(mappedBy = "estacionamento", fetch = LAZY, cascade = PERSIST)` |

### `Vaga` (`vagas`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idVaga` | `Integer` | `@Id`, Sequence |
| `estacionamento` | `Estacionamento` | `@ManyToOne(fetch = EAGER)`, lado dono da FK |
| `tipoVaga` | `TipoVaga` | `@Enumerated(EnumType.STRING)` (`COMUM`, `IDOSO`, `PCD`) |

### `Estadia` (`estadias`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idEstadia` | `Long` | `@Id`, Sequence (única com `Long`) |
| `vaga` | `Vaga` | `@ManyToOne` |
| `carro` | `Carro` | `@ManyToOne` |
| `horarioEntrada` | `LocalDateTime` | `@Column(nullable = false)` |
| `horarioSaida` | `LocalDateTime` | `@Column(nullable = true)` — `null` indica estadia ativa |
| `valor` | `BigDecimal` | `@Column(precision = 10, scale = 2)` |

---

## 📦 Camada de Dados (Repositories)

Os 4 repositories estendem `JpaRepository` e utilizam o padrão de **Query Methods** do Spring Data:

```java
// CarroRepository
Optional<Carro> findByPlaca(String placa);
boolean existsByPlaca(String placa);

// EstacionamentoRepository
Optional<Estacionamento> findByNomeIgnoreCase(String nome);
boolean existsByNomeIgnoreCase(String nome);

// EstadiaRepository
boolean existsByCarro_PlacaAndHorarioSaidaIsNull(String carroPlaca);
Optional<Estadia> findByCarro_PlacaAndHorarioSaidaIsNull(String carroPlaca);

// VagaRepository
List<Vaga> findByEstacionamento_IdEstacionamento(Integer idEstacionamento, Pageable pageable);
List<Vaga> findByEstacionamento_IdEstacionamentoAndTipoVaga(Integer idEstacionamento, TipoVaga tipoVaga, Pageable pageable);
```

---

## 🚧 O que falta fazer (Roteiro Sequencial)

### Fase 1 — Dependências e Setup Web
- [x] Adicionar a dependência do Spring MVC (`spring-boot-starter-webmvc`) ao `pom.xml`.

### Fase 2 — Módulo Carro (concluído, pendente de validação manual)
- [x] `CarroRequestDTO` e `CarroDTO` como `record`.
- [x] `CarroService`: `findCarroByPlaca` e `register`.
- [x] `CarroController`: `GET /carros/{placa}` e `POST /carros`.
- [x] `GlobalExceptionHandler` e `ApiError`.
- [ ] Executar o roteiro de testes manuais (seção **API**).
- [ ] Teste unitário do `CarroService` com JUnit 5 e Mockito (placa em minúsculas, placa nula, placa duplicada, carro não encontrado).

### Fase 3 — Módulo Estacionamento e Vagas
- [ ] **Inicializar a lista `vagas` em `Estacionamento`** (hoje o campo nasce `null` e causaria `NullPointerException` ao montar o pátio).
- [ ] Decidir se `Vaga` ganha um campo `numero` (identificação da vaga dentro do pátio). É mais barato decidir agora do que migrar depois.
- [ ] DTO de cadastro de `Estacionamento` (nome do pátio e quantidade de vagas comuns/idoso/PCD).
- [ ] `EstacionamentoService`: validar nome (nulo/em branco), checar duplicidade com `existsByNomeIgnoreCase`, validar quantidades (não negativas), criar as vagas, **ligar os dois lados do relacionamento** (`vaga.setEstacionamento` e adicionar na lista) e salvar numa única transação.
- [ ] `EstacionamentoController`.
- [ ] Query JPQL customizada (`@Query`) para buscar vagas livres por tipo em um pátio usando `NOT EXISTS`.

### Fase 4 — Módulo Estadia
- [ ] `EstadiaService` com as regras:
  - Validar elegibilidade do carro para vaga de idoso/PCD.
  - Impedir que um veículo com estadia ativa (`horarioSaida == null`) entre novamente.
  - **Impedir que a mesma vaga receba duas estadias ativas** (novo método no `EstadiaRepository`, ex.: `existsByVagaAndHorarioSaidaIsNull`).
  - Impedir entrada se o estacionamento estiver com todas as vagas ocupadas.
  - Registrar entrada com `LocalDateTime.now()` automático (reaproveitando carro existente pela placa).
  - Registrar saída e calcular a cobrança com base em faixas de tempo (até 1h, até 2h, até 4h, acima de 4h).
  - Validar e impedir `horarioSaida` anterior ao `horarioEntrada`.
- [ ] Avaliar índice único parcial no banco (uma estadia ativa por vaga) como defesa definitiva contra requisições simultâneas.
- [ ] Endpoints previstos:
  - `POST /easypark/estacionamentos/{id}/entrada` — registrar entrada de veículo.
  - `PATCH /easypark/estacionamentos/{id}/saida` — registrar saída e calcular valor cobrado.
  - `GET /easypark/estacionamentos/{id}/vagas/livres` e `/ocupadas`.
  - `GET /easypark/estacionamentos/{id}/veiculos-estacionados` — carros no pátio no momento.
  - `GET /easypark/estacionamentos/{id}/historico` — histórico completo de estadias.

### Fase 5 — Validações e Tratamento de Exceções
- [ ] Aplicar Bean Validation (`@NotBlank`, `@Size`, `@Pattern` para placas no padrão antigo e Mercosul) nos DTOs, incluindo os limites das colunas (placa até 10, modelo até 50, cor até 30).
- [ ] Criar exceções de negócio específicas quando necessário (ex.: `VagaOcupadaException`, `VeiculoNaoElegivelException`, `EstadiaAtivaExistenteException`) e mapeá-las no `GlobalExceptionHandler`.
- [ ] Tratar `MethodArgumentNotValidException` (Bean Validation) e JSON malformado de forma padronizada.
- [ ] Avaliar um handler genérico de `Exception` (500) só depois de tratar os casos que o Spring já resolve bem.

### Fase 6 — Melhorias e Evoluções Futuras
- [ ] Documentação da API via Swagger / OpenAPI (`springdoc-openapi`).
- [ ] Ajustar `allocationSize` dos `@SequenceGenerator` para otimizar os lotes de escrita das tabelas `Vaga` e `Estadia`.
- [ ] Revisar o `fetch` dos `@ManyToOne` (a `Estadia` usa o padrão `EAGER`, e a `Vaga` declara `EAGER` explicitamente).
- [ ] Endpoint de listagem de carros com paginação.
- [ ] Migrações com Flyway ou Liquibase no lugar de `ddl-auto=update`.
- [ ] Docker / Docker Compose para subir o container PostgreSQL sem dependência do ambiente local.
- [ ] Spring Security + JWT para autenticação de operadores do estacionamento.
- [ ] Testes de integração com Testcontainers.

---

## ⚙️ Configuração do Ambiente Local

- **Banco de Dados:** PostgreSQL (banco `easy_park_db`).
- **Variáveis de Ambiente:** Configure as seguintes variáveis na sua IDE / Run Configuration para se conectar ao banco local:
  - `DB_HOST` (ex: `localhost:5432`)
  - `DB_NAME` (ex: `easy_park_db`)
  - `DB_USER` (ex: `postgres`)
  - `DB_PASSWORD` (ex: `sua_senha`)
- **DDL Hibernate:** Configurado como `spring.jpa.hibernate.ddl-auto=update`, garantindo que o esquema e as sequências do banco sejam criados/atualizados automaticamente no start da aplicação.

### ⚠️ Limitação do `ddl-auto=update`

O modo `update` só **adiciona** tabelas e colunas; ele **não renomeia nem remove** nada. Ao renomear um campo ou uma coluna na entidade, o banco existente fica com a coluna antiga (que continua `NOT NULL`/`UNIQUE`) e o Hibernate cria uma nova, o que quebra os `INSERT`s. Nesses casos, renomeie a coluna manualmente antes de subir a aplicação.

Exemplo real deste projeto: a coluna do nome do pátio passou de `estacionamento` para `nome_estacionamento`. Em bancos criados antes dessa mudança, rode no pgAdmin, com a aplicação parada:

```sql
ALTER TABLE public.estacionamentos
    RENAME COLUMN estacionamento TO nome_estacionamento;
```

Em um banco novo (criado do zero), nada precisa ser feito.

---

## 🧭 Como rodar

1. Crie o banco `easy_park_db` no PostgreSQL.
2. Defina as variáveis de ambiente `DB_HOST`, `DB_NAME`, `DB_USER` e `DB_PASSWORD`.
3. Na pasta `easypark/`, execute `./mvnw spring-boot:run` (ou rode a classe `EasyparkApplication` pela IDE).
4. A API responde em `http://localhost:8080/easypark/...` (porta padrão do Spring Boot).

---

*Documento mantido e atualizado de acordo com o progresso real do projeto EasyPark.*
