# 🚗 EasyPark

API REST para gerenciamento de estacionamentos, vagas e estadias de veículos, desenvolvida em Java com Spring Boot — evolução de um sistema originalmente feito em Portugol.

> 📌 Este README documenta o **progresso real do projeto**: decisões de modelagem já tomadas, o que já foi implementado e o roteiro do que falta. Serve como guia de continuidade para quem está aprendendo Spring Boot, MVC e JPA na prática.

---

## 📖 Sobre o projeto

O EasyPark gerencia **múltiplos estacionamentos** (pátios), cada um com vagas comuns, de idoso e PCD. O sistema deve:

- Cadastrar estacionamentos, informando a quantidade de vagas comuns, de idoso e PCD
- Registrar a entrada e saída de veículos automaticamente (horário do servidor, não informado pelo cliente)
- Impedir estacionar em vaga de idoso/PCD sem elegibilidade
- Impedir a mesma placa estacionada duas vezes simultaneamente
- Calcular o valor cobrado com base no tempo de permanência
- Listar veículos estacionados, histórico e ocupação por estacionamento

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 4.1.1 | Framework |
| Spring Data JPA | Persistência |
| PostgreSQL | Banco de dados |
| Lombok | Redução de boilerplate |
| Maven | Build e dependências |

---

## 🏗️ Arquitetura (MVC em camadas)

```
com.br.lucasthest8ic.easypark/
├── model/          ✅ criado (Carro, Vaga, Estacionamento, Estadia)
├── enums/          ✅ criado (TipoVaga)
├── repository/     ⬜ não criado ainda
├── service/        🟡 pasta criada, vazia
├── controller/     ⬜ não criado ainda
└── dto/            ⬜ não criado ainda
```

---

## 🧠 Decisões de modelagem já fechadas

Estas decisões foram pensadas com calma antes de codar — vale manter aqui como registro do *porquê*, não só do *o quê*:

- **`Vaga` não guarda mais o carro atual nem um campo `vagaLivre`.** Ambos eram dados deriváveis (existe uma `Estadia` ativa? → ocupada) e foram removidos para evitar dessincronização.
- **`Estadia` é a entidade de associação** entre `Carro` e `Vaga`, carregando `horarioEntrada`, `horarioSaida` e `valor`. Não existe campo booleano de "ativo" — isso é deduzido por `horarioSaida == null`.
- **Horários são sempre gerados pelo servidor** (`LocalDateTime.now()`), nunca informados pelo cliente — elimina a necessidade de validar "entrada até 24h no passado".
- **Regra de negócio confirmada:** não permitir horário de saída anterior ao horário de entrada.
- **`Carro` é reaproveitado**, não duplicado: ao chegar uma placa já existente, busca-se o registro (por isso `placa` é `unique`), em vez de criar um novo.
- **`totalVagas` e `totalVagasIdoso` não existem como campos** em `Estacionamento` — são deriváveis de `vagas.size()` e da contagem por `tipoVaga`. A quantidade informada no cadastro (ex: "50 comuns, 10 idoso, 5 PCD") será tratada via **DTO**, usada só para gerar os objetos `Vaga` no momento da criação.
- **Tipos de ID definidos por volume de crescimento:**
  - `Carro`, `Vaga`, `Estacionamento` → `Integer` (crescimento lento/catálogo)
  - `Estadia` → `Long` (cresce a cada entrada, para sempre)
- **`TipoVaga` é um enum simples** (`COMUM`, `IDOSO`, `PCD`), sem construtor — vive no pacote `enums` (não pôde ser `enum` no singular por ser palavra reservada do Java).
- **Estratégia de geração de ID: `GenerationType.SEQUENCE`**, com `@SequenceGenerator` explícito por entidade (nome, sequência, `initialValue`, `allocationSize`) — escolhida por ter melhor suporte a *batch insert* no PostgreSQL, relevante para o crescimento de `Estadia`.

---

## ✅ O que já foi feito

### `Carro` — ✅ completo como entidade JPA
- `@Entity`, `@Table(name = "carros")`
- `@Id` + `@SequenceGenerator` + `@GeneratedValue(strategy = SEQUENCE)` configurados explicitamente
- `@Column` com `unique = true` em `placa` (suporta a regra de reaproveitar carro)
- Lombok aplicado: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`

### `TipoVaga` (enum) — ✅ completo
- Enum simples, sem construtor: `COMUM`, `IDOSO`, `PCD`

### `Vaga` — 🟡 parcial
- Campos definidos: `idVaga`, `estacionamento`, `tipoVaga`
- `@Enumerated(EnumType.STRING)` já aplicado no campo `tipoVaga`
- **Ainda não é `@Entity`** — falta anotar como entidade e configurar o relacionamento com `Estacionamento`

### `Estacionamento` — 🟡 parcial
- Campos definidos: `idEstacionamento`, `nome`, `vagas`
- **Ainda não é `@Entity`** — falta anotar e configurar o lado `@OneToMany` da relação com `Vaga`

### `Estadia` — 🟡 parcial
- Campos definidos: `idEstadia`, `vaga`, `carro`, `valor`, `horarioEntrada`, `horarioSaida`
- **Ainda não é `@Entity`** — falta anotar e configurar os dois `@ManyToOne`

### Estrutura de pastas
- `model` e `enums` criados e em uso
- `service` criada, mas ainda vazia

---

## 🚧 O que falta fazer (roteiro sequencial)

### Fase 1 — Fechar a camada JPA
- [ ] Anotar `Estacionamento` e `Vaga` juntos (relação bidirecional `@OneToMany` / `@ManyToOne` + `mappedBy`)
- [ ] Decidir e aplicar `@SequenceGenerator` em `Estacionamento`, `Vaga` e `Estadia` (mesmo padrão usado em `Carro`)
- [ ] Anotar `Estadia` (`@ManyToOne` para `Vaga` e para `Carro`)
- [ ] Aplicar Lombok (`@Getter`/`@Setter`/construtores) nas 3 entidades restantes

### Fase 2 — Conectar o banco
- [ ] Configurar `application.properties` com URL, usuário e senha do PostgreSQL
- [ ] Subir a aplicação e validar que o Hibernate cria as 4 tabelas sem erro

### Fase 3 — Camada de dados
- [ ] Criar pacote `repository`
- [ ] Criar `CarroRepository` (`JpaRepository`), incluindo busca por placa (`findByPlaca`/`existsByPlaca`)
- [ ] Criar `EstacionamentoRepository`, `VagaRepository`, `EstadiaRepository`

### Fase 4 — DTOs
- [ ] Criar pacote `dto`
- [ ] DTO de cadastro de `Estacionamento` (nome + quantidade de vagas comuns/idoso/PCD — não persiste esses números, só usa para gerar as `Vaga`)
- [ ] DTOs de request/response para `Carro`

### Fase 5 — Regras de negócio (Service)
- [ ] Criar `Estacionamento` a partir do DTO, gerando automaticamente as `Vaga` correspondentes
- [ ] Regra: elegibilidade do carro para vaga idoso/PCD
- [ ] Regra: impedir mesma placa estacionada duas vezes ao mesmo tempo
- [ ] Regra: impedir ultrapassar o limite de vagas
- [ ] Regra: registrar entrada com `LocalDateTime.now()` automático
- [ ] Regra: calcular valor na saída, por faixa de tempo
- [ ] Regra: impedir horário de saída anterior ao de entrada
- [ ] Validação de placa via regex (formato antigo + Mercosul), aplicada no DTO com `@Pattern`

### Fase 6 — Controller (Endpoints)
- [ ] CRUD de `Estacionamento` (incluindo cadastro com quantidade de vagas)
- [ ] CRUD de `Carro`
- [ ] `POST /estacionamentos/{id}/entrada` — registrar entrada
- [ ] `PATCH /estacionamentos/{id}/saida` — registrar saída + cálculo de valor
- [ ] `GET /estacionamentos/{id}/vagas/livres` e `/ocupadas`
- [ ] `GET /estacionamentos/{id}/veiculos-estacionados` — lista de carros e vagas atuais
- [ ] `GET /estacionamentos/{id}/historico` — histórico de estadias

### Fase 7 — Tratamento de exceções
- [ ] `@ControllerAdvice` + `@ExceptionHandler` para os erros de regra de negócio (vaga ocupada, placa duplicada, estacionamento lotado, veículo não encontrado)

### Fase 8 — Melhorias futuras
- [ ] Swagger/OpenAPI
- [ ] Paginação e filtros (por placa, por data)
- [ ] Testes unitários e de integração
- [ ] Docker Compose para o PostgreSQL
- [ ] Spring Security + JWT (login de funcionários)

---

## 🔎 Pendência técnica em aberto

- Decidir se `Estacionamento`/`Vaga`/`Estadia` vão seguir exatamente o mesmo padrão de `@SequenceGenerator` já validado em `Carro`, ou se cada uma terá `allocationSize` diferente pensando no volume de crescimento (ex: `Estadia` pode se beneficiar de um `allocationSize` maior por crescer continuamente).

---

*Documento gerado a partir do progresso real do projeto — atualize conforme cada fase for concluída.*
