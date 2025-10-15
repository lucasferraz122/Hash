# Análise de Desempenho de Tabelas Hash em Java

## Instituição

- **Nome da Instituição**: PUCPR - Pontifícia Universidade Católica do Paraná
- **Disciplina**: Resolução de Problemas Estruturados em Computação
- **Professor**: Andrey Cabral


## Alunos do Grupo em Ordem Alfabética
- Aluno 1: Alan Filipe Reginato de França Santos - GitHub: lipeerz.
- Aluno 2: Lucas Ferraz dos Santos - GitHub: lucasferraz122.
- Aluno 3: Pedro Henrique Moraes - GitHub: PedroMoraes12.

## Objetivo do Trabalho

Este trabalho implementa e analisa o desempenho de diferentes estratégias de tabelas hash em Java, comparando métodos de tratamento de colisões e funções hash para conjuntos de dados de diversos tamanhos.

## Estrutura do Projeto

## Arquivos Principais

- `Main.java` → Classe principal com geração de dados, execução de testes e exportação de resultados
- `Registro.java` → Classe que representa um registro com código de 9 dígitos
- `TabelaHash.java` → Classe abstrata base para implementações de tabelas hash
- `HashEncadeamento.java` → Implementação com tratamento de colisões por encadeamento
- `HashAberto.java` → Implementação com tratamento de colisões por hashing aberto (rehashing)
- `ListaEncadeada.java` → Estrutura de lista encadeada para o método de encadeamento

## Estratégias Implementadas

### Funções Hash

1. **Divisão**: `h(k) = k mod m`
2. **Multiplicação**: `h(k) = ⌊m × (k × A mod 1)⌋` onde `A = 0.618`
3. **Soma de Dígitos**: Soma dos valores ASCII dos caracteres mod m

### Métodos de Tratamento de Colisões

- **Encadeamento**: Listas encadeadas em cada bucket
- **Hashing Aberto**:
  - **Sondagem Linear**: `h(k, i) = (h(k) + i) mod m`
  - **Sondagem Quadrática**: `h(k, i) = (h(k) + i²) mod m`
  - **Hash Duplo**: `h(k, i) = (h₁(k) + i × h₂(k)) mod m`

## Metodologia Experimental

### Configurações Testadas

- **Tamanhos de Tabela**: 1.000, 10.000, 100.000 posições
- **Conjuntos de Dados**: 100.000, 1.000.000, 10.000.000 registros
- **Seed Fixa**: 12345 para garantir reproducibilidade

### Métricas Coletadas

- Tempo de inserção (milissegundos)
- Número de colisões
- Tempo de busca (milissegundos)
- Elementos encontrados na busca
- Três maiores listas encadeadas (apenas encadeamento)

## Como Executar

### Pré-requisitos

- Java JDK 8 ou superior
- Terminal/Command Prompt

### Execução do Projeto

1. **Compilar o código**:
```bash
javac Main.java
