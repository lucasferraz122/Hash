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

# Análise de Desempenho das Tabelas Hash

## Configuração dos Testes

Foram testadas duas abordagens principais:

- **Encadeamento (enc)**
- **Endereçamento Aberto (ab)** com três variações de função de rehash

**Configurações:**
- **Tamanho da tabela:** 1000  
- **Tamanho dos dados:** 100.000 e 1.000.000 elementos  
- **Funções hash testadas:** hash 1, hash 2 e hash 3  

---

## Resultados — 100.000 dados

### Tempo de Inserção (ms)

| Tipo | Hash | Tempo Médio |
|------|------|-------------|
| enc  | 1    | 44,0        |
| enc  | 2    | 12,2        |
| enc  | 3    | 15,8        |
| ab   | 1    | 3340,0      |
| ab   | 2    | 3200,0      |
| ab   | 3    | 6670,0      |

**Discussão:**  
As tabelas de **encadeamento** foram **muito mais rápidas** na inserção, com destaque para a função hash 2.  
As tabelas com **endereçamento aberto** apresentaram desempenho bem inferior, indicando forte impacto das colisões.

---

### Tempo de Busca (ms)

| Tipo | Hash | Tempo Médio |
|------|------|-------------|
| enc  | 1    | 250,9       |
| enc  | 2    | 292,7       |
| enc  | 3    | 10808,6     |
| ab   | 1    | 3930,0      |
| ab   | 2    | 3770,0      |
| ab   | 3    | 7740,0      |

**Discussão:**  
A busca também favoreceu o **encadeamento**, principalmente nas funções hash 1 e 2.  
A função hash 3 teve desempenho ruim, indicando **mau espalhamento** e **maior número de colisões**.

---

### Colisões

| Tipo | Hash | Colisões |
|------|------|----------|
| enc  | 1    | 99.000   |
| enc  | 2    | 99.023   |
| enc  | 3    | 99.934   |
| ab   | 1–3  | ~99.000.000 |

**Discussão:**  
O **endereçamento aberto** apresentou um **número altíssimo de colisões**, justificando o tempo elevado.  
Já o **encadeamento** teve valores estáveis e bem menores.

---

## Resultados — 1.000.000 dados

### Tempo de Inserção (ms)

| Tipo | Hash | Tempo Médio |
|------|------|-------------|
| enc  | 1    | 226,7       |
| enc  | 2    | 299,3       |
| enc  | 3    | 1580,0      |
| ab   | 1    | 40598,8     |
| ab   | 2    | 35807,8     |
| ab   | 3    | 68251,3     |

### Tempo de Busca (ms)

| Tipo | Hash | Tempo Médio |
|------|------|-------------|
| enc  | 1    | 63858,1     |
| enc  | 2    | 61643,8     |
| enc  | 3    | 10808,6     |
| ab   | 1    | 61376,8     |
| ab   | 2    | 60488,4     |
| ab   | 3    | 86417,5     |

### Colisões

| Tipo | Hash | Colisões |
|------|------|----------|
| enc  | 1    | 999.000  |
| enc  | 2    | 999.012  |
| enc  | 3    | 999.934  |
| ab   | 1–3  | ~999.000.000 |

---

## Discussão Geral

- O **encadeamento** apresenta tempos de inserção e busca significativamente melhores do que o endereçamento aberto.  
- Funções hash diferentes impactam o desempenho; a **hash 2** geralmente distribui melhor os elementos, resultando em menos colisões.  
- O **endereçamento aberto** sofre com colisões massivas, especialmente em grandes volumes de dados, tornando-o mais lento.  
- Funções hash ruins (como hash 3 no encadeamento) aumentam consideravelmente o tempo de busca.  

**Conclusão:**  
Para grandes volumes de dados, o **encadeamento com função hash bem escolhida** é a abordagem mais eficiente.  
O **endereçamento aberto** precisa de otimizações ou tabelas maiores para ter desempenho aceitável.


## Como Executar

### Pré-requisitos

- Java JDK 8 ou superior
- Terminal/Command Prompt

### Execução do Projeto

1. **Compilar o código**:
```bash
javac Main.java
