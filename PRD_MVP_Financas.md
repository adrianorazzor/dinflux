# 📄 PRD – MVP App de Controle Financeiro

## 1. Visão Geral
O objetivo do MVP é substituir e melhorar a planilha atual de controle financeiro, oferecendo uma aplicação web simples, clara e rápida.  
O app deve permitir registrar **ingressos**, **despesas fixas e variáveis** e **gastos no cartão de crédito**, garantindo:
- visão consolidada de fluxo de caixa do mês,  
- acompanhamento das faturas de cartão,  
- destaque de recorrências (despesas fixas/assinaturas),  
- total de despesas por categoria.

---

## 2. Regras de Negócio (mais importante)

### 2.1 Contas
- Tipos de contas:
  - **Banco/Carteira** → onde ingressos e despesas em dinheiro/transferência são registrados.  
  - **Cartão de Crédito** → onde despesas pagas no cartão são registradas.  
- Cartão possui atributos extras: **limite**, **dia de fechamento** e **dia de vencimento**.

### 2.2 Transações
- Tipos de transações:  
  - **Income**: entrada de dinheiro (salário, auxílios).  
  - **Expense**: saída de dinheiro (despesa).  
  - **Transfer**: movimentação entre contas (ex.: pagamento de fatura: Banco → Cartão).  
- Campos: data, descrição, valor, conta, categoria, tags.  
- **Valor sempre positivo**; o sinal é dado pelo tipo (`income` soma, `expense` subtrai).  

### 2.3 Recorrências
- Uma transação pode ser marcada como **recorrente** (mensal/semanal).  
- Recorrências são vinculadas à conta correta:
  - Se paga via banco/efetivo → recorrência em conta bancária.  
  - Se paga via cartão → recorrência no cartão (lançada direto na fatura do ciclo).  

### 2.4 Cartão de Crédito
- Toda despesa registrada em conta cartão é automaticamente atribuída a uma **fatura** (definida pelo fechamento/vencimento).  
- **Pagamento da fatura**:
  - gera transação `transfer` (banco → cartão).  
  - quita ou reduz a fatura.  
- **Itens do cartão não aparecem em “despesas cash”**, apenas na fatura.

### 2.5 Dashboard (visão mensal)
- KPIs principais:
  - **Ingressos**: soma de incomes do mês.  
  - **Despesas (cash)**: despesas em banco/efetivo.  
  - **Cartão**: total de pagamentos de fatura feitos no mês.  
  - **Saldo** = Ingressos – Despesas (cash) – Cartão.  
- Extras:
  - Calendário de caixa (vencimentos da semana).  
  - Gráfico pizza de despesas por categoria.  

### 2.6 Categorias
- Cada transação pode ser associada a uma categoria (Saúde, Transporte, etc.).  
- Permite análise de gastos por categoria **na data da compra**, independente do meio de pagamento.

### 2.7 Importação / Exportação
- MVP aceita importação de CSV (ex.: colar dados da planilha).  
- Exportação de transações para CSV também disponível.

---

## 3. Requisitos Técnicos

### 3.1 Stack
- **Backend**:  
  - **Kotlin + Ktor** (HTTP server, rotas).  
  - **Exposed** (ORM/Kotlin SQL DSL) ou queries nativas.  
  - **Flyway** para migrations.  
  - **JUnit + Kotest** para testes de regra de negócio.  

- **Frontend**:  
  - **HTMX** para interatividade server-driven (sem SPA pesada).  
  - **TailwindCSS** para estilo rápido e responsivo.  
  - **Thymeleaf** ou `kotlinx.html` para templates HTML.  

- **Banco de Dados**:  
  - **PostgreSQL**.  
  - Índices em `(user_id, date)` e `(account_id, date)` para performance.  

- **Infra**:  
  - Deploy em **Fly.io**.  
  - Autenticação via sessions.  
  - Estrutura multiusuário desde o início.  

### 3.2 Estrutura de Dados (mínimo)
- **users**: credenciais, moeda base.  
- **accounts**: contas (banco/carteira/cartão).  
- **transactions**: lançamentos (income/expense/transfer).  
- **statements**: faturas de cartão.  
- **recurrences**: recorrências de transações.  
- **categories**: categorias de despesas/receitas.  

### 3.3 Funcionalidades por Sprint
- **Sprint 1**:  
  - CRUD de contas, categorias, lançamentos simples.  
  - Dashboard com KPIs básicos.  
- **Sprint 2**:  
  - Faturas de cartão (criação automática, pagamento).  
  - Recorrências.  
- **Sprint 3**:  
  - Dashboard com calendário e pizza por categoria.  
  - Import/Export CSV.  

---

## 4. Requisitos Não Funcionais
- **Segurança**: dados criptografados em trânsito (HTTPS) e em repouso (Postgres com disk encryption).  
- **Performance**: operações principais (<200ms).  
- **Internacionalização**: labels e categorias seed em PT/ES.  
- **Testabilidade**: testes unitários para regras de fatura e recorrência.  

---

## 5. Checklist de Desenvolvimento

- [x] Implementar autenticação backend (rotas de registro/login, hashing de senha, armazenamento de sessão e middleware).
- [x] Construir página de login com HTMX/Tailwind e layout base integrado às rotas de autenticação.
- [ ] Proteger rotas existentes com verificação de sessão e adicionar fluxo de logout.
- [ ] Adicionar CRUD de contas (rotas, serviços, formulários, validações e testes conforme Sprint 1).
- [ ] Adicionar CRUD de categorias (mesma abordagem, garantindo escopo por usuário e testes).
- [ ] Adicionar CRUD de transações simples (income/expense/transfer) implementando regras de tipo e consultas Exposed.
- [ ] Renderizar KPIs mensais do dashboard (ingressos, despesas cash, cartão, saldo) suportados por consultas testadas.
- [ ] Escrever testes de integração/unidade para autenticação e CRUD; executar `./gradlew :server:test` e registrar resultados.
- [ ] Documentar novos endpoints/fluxos e anotar necessidades de configuração/ambiente.
