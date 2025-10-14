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
