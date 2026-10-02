# 🚗 EasyPark

API REST para gerenciamento de estacionamentos, vagas e estadias de veículos, desenvolvida em Java com Spring Boot — evolução de um sistema originalmente feito em Portugol.

> ⚠️ **Projeto em fase de estruturação e aprendizado.**
> Este é um projeto de estudos focado em Spring Boot, JPA e arquitetura em camadas. As decisões aqui documentadas refletem o processo de aprendizado na prática — a camada de persistência/banco está concluída, mas **ainda não há Service nem Controller implementados**, portanto o sistema ainda não está funcional de ponta a ponta. O objetivo é consolidar a modelagem e a persistência antes de avançar para as regras de negócio.

---

## 📖 Sobre o projeto

O **EasyPark** gerencia múltiplos estacionamentos (pátios), cada um com vagas comuns, de idoso e PCD. O sistema foi projetado para:

- Cadastrar estacionamentos informando a quantidade de vagas por categoria (comuns, idoso e PCD).
- Registrar a entrada e saída de veículos automaticamente com horário gerado pelo servidor (`LocalDateTime.now()`).
- Impedir estacionamento em vagas reservadas (idoso/PCD) para veículos não elegíveis.
- Impedir que a mesma placa esteja estacionada em duas estadias ativas simultaneamente.
- Calcular o valor cobrado com base no tempo de permanência no momento da saída.
- Consultar vagas livres/ocupadas, veículos atualmente estacionados e histórico de estadias.

---

## 🛠️ Tecnologias e Stack

| Tecnologia | Função / Uso |
|---|---|
| **Java 25** | Linguagem de programação |
| **Spring Boot 4.1.1** | Framework base da aplicação |
| **Spring Data JPA** | Abstração de persistência de dados |
| **Hibernate 7** | ORM / Provedor JPA |
| **PostgreSQL** | Banco de dados relacional |
| **Lombok** | Redução de código boilerplate (`@Getter`, `@Setter`, etc.) |
| **Maven** | Gerenciador de dependências e build |

---

## 📊 Status geral do projeto

| Camada / Componente | Status | Detalhes |
|---|---|---|
| **Modelagem (Entidades)** | ✅ Concluída | `Carro`, `Estacionamento`, `Vaga`, `Estadia` criadas |
| **JPA (Anotações / Relacionamentos)** | ✅ Concluída | `@OneToMany`, `@ManyToOne`, `@SequenceGenerator` aplicados |
| **Banco de Dados (PostgreSQL)** | ✅ Conectado | Tabelas criadas e atualizadas automaticamente via Hibernate |
| **Repositories** | ✅ Concluída (4/4) | Spring Data JPA interfaces com Query Methods criadas |
| **Dependência Web (Spring MVC)** | ⬜ Pendente | Adicionar starter web no `pom.xml` |
| **DTOs** | ⬜ Não iniciada | Estruturação de DTOs de entrada e saída |
| **Service (Regras de Negócio)** | 🟡 Pasta criada | Lógica de entrada/saída, cálculo de valor e exceções pendentes |
| **Controller (Endpoints REST)** | ⬜ Não iniciada | Exposição das rotas HTTP |
| **Validações (Bean Validation)** | ⬜ Não iniciada | Anotações `@NotBlank`, `@Pattern`, `@Size` |
| **Tratamento Global de Exceções** | ⬜ Não iniciada | `@RestControllerAdvice` e `@ExceptionHandler` |
| **Testes Unitários / Integração** | ⬜ Não iniciada | Testes com JUnit 5 / Mockito |

---

## 🏗️ Arquitetura (MVC em Camadas)

```
com.br.lucasthest8ic.easypark/
├── model/           ✅ completo (Carro, Vaga, Estacionamento, Estadia)
├── enums/           ✅ completo (TipoVaga)
├── repository/      ✅ completo (CarroRepository, EstacionamentoRepository, VagaRepository, EstadiaRepository)
├── service/         🟡 pasta criada (vazia — aguardando regras de negócio)
├── controller/      ⬜ não criado ainda
└── dto/             ⬜ não criado ainda
```

---

## 🧠 Decisões de modelagem e por quê

A modelagem do domínio foi construída questionando continuamente se cada informação era um **dado de origem** (precisa ser armazenado na tabela) ou **dado derivado** (pode ser calculated a partir de outros dados já existentes):

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

---

## 🗄️️ Detalhamento do Modelo de Dados

### `Carro` (`carros`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idCarro` | `Integer` | `@Id`, Sequence |
| `placa` | `String` | `@Column(unique = true, length = 10)` |
| `modelo` | `String` | `@Column(length = 50)` |
| `cor` | `String` | `@Column(length = 30)` |
| `elegivelVagaIdoso` | `boolean` | Dado de origem (não derivável) |

### `Estacionamento` (`estacionamentos`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idEstacionamento` | `Integer` | `@Id`, Sequence |
| `nome` | `String` | `@Column(unique = true)` (busca case-insensitive no repo) |
| `vagas` | `List<Vaga>` | `@OneToMany(mappedBy = "estacionamento", fetch = LAZY, cascade = PERSIST)` |

### `Vaga` (`vagas`)
| Campo | Tipo JPA / Java | Configuração JPA / Observação |
|---|---|---|
| `idVaga` | `Integer` | `@Id`, Sequence |
| `estacionamento` | `Estacionamento` | `@ManyToOne(fetch = EAGER)`, Lado dono da FK |
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

Os 4 repositories foram criados estendendo `JpaRepository` e utilizam o padrão de **Query Methods** do Spring Data:

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
- [ ] Adicionar a dependência do Spring MVC (`spring-boot-starter-web`) ao `pom.xml`.

### Fase 2 — DTOs (Data Transfer Objects)
- [ ] Criar pacote `dto`.
- [ ] DTO de cadastro de `Estacionamento` (recebe o nome do pátio e a quantidade desejada de vagas comuns/idoso/PCD para geração automática no Service).
- [ ] DTOs de requisição e resposta para `Carro`, `Vaga` e `Estadia`.

### Fase 3 — Regras de Negócio (Service)
- [ ] Criar `EstacionamentoService`: criar o pátio e instanciar automaticamente a lista de objetos `Vaga` com base no DTO.
- [ ] Criar `EstadiaService` e implementar as regras:
  - Validar elegibilidade do carro para vaga de idoso/PCD.
  - Impedir que um veículo com estadia ativa (`horarioSaida == null`) entre novamente.
  - Impedir entrada se o estacionamento estiver com todas as vagas ocupadas.
  - Registrar entrada com `LocalDateTime.now()` automático.
  - Registrar saída e calcular a cobrança com base em faixas de tempo (até 1h, até 2h, até 4h, acima de 4h).
  - Validar e impedir `horarioSaida` anterior ao `horarioEntrada`.
- [ ] Escrever Query JPQL customizada (`@Query`) para buscar vagas livres por tipo em um determinado pátio usando `NOT EXISTS`.

### Fase 4 — Controllers (Endpoints REST)
- [ ] CRUD de `Estacionamento` e `Carro`.
- [ ] `POST /estacionamentos/{id}/entrada` — Registrar entrada de veículo.
- [ ] `PATCH /estacionamentos/{id}/saida` — Registrar saída e calcular valor cobrado.
- [ ] `GET /estacionamentos/{id}/vagas/livres` e `/ocupadas`.
- [ ] `GET /estacionamentos/{id}/veiculos-estacionados` — Listagem de carros no pátio no momento.
- [ ] `GET /estacionamentos/{id}/historico` — Histórico completo de estadias.

### Fase 5 — Validações e Tratamento de Exceções
- [ ] Aplicar Bean Validation (`@NotBlank`, `@Size`, `@Pattern` para placas no padrão antigo e Mercosul) nos DTOs.
- [ ] Criar exceções customizadas de negócio (ex: `VagaOcupadaException`, `VeiculoNaoElegivelException`, `EstadiaAtivaExistenteException`).
- [ ] Configurar `@RestControllerAdvice` e `@ExceptionHandler` para padronizar as respostas de erro da API HTTP.

### Fase 6 — Melhorias e Evoluções Futuras
- [ ] Documentação da API via Swagger / OpenAPI (`springdoc-openapi`).
- [ ] Ajustar `allocationSize` dos `@SequenceGenerator` para otimizar os lotes de escrita das tabelas `Vaga` e `Estadia`.
- [ ] Docker / Docker Compose para subir o container PostgreSQL sem dependência do ambiente local.
- [ ] Spring Security + JWT para autenticação de operadores do estacionamento.
- [ ] Testes unitários e de integração com JUnit 5, Mockito e Testcontainers.

---

## ⚙️ Configuração do Ambiente Local

- **Banco de Dados:** PostgreSQL (banco `easy_park_db`).
- **Variáveis de Ambiente:** Configure as seguintes variáveis na sua IDE / Run Configuration para se conectar ao banco local:
  - `DB_HOST` (ex: `localhost:5432`)
  - `DB_NAME` (ex: `easy_park_db`)
  - `DB_USER` (ex: `postgres`)
  - `DB_PASSWORD` (ex: `sua_senha`)
- **DDL Hibernate:** Configurado como `spring.jpa.hibernate.ddl-auto=update`, garantindo que o esquema e as sequências do banco sejam criados/atualizados automaticamente no start da aplicação.

---

*Documento mantido e atualizado de acordo com o progresso real do projeto EasyPark.*
