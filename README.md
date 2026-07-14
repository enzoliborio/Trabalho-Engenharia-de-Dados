# 💾 Trabalho Prático - Engenharia de Dados

Este repositório contém as implementações do trabalho prático final da disciplina de Engenharia de Dados (Semestre 2026.1) do curso de Engenharia de Computação da Universidade Federal de Sergipe (UFS).

O projeto é dividido em duas etapas de integração de aplicações Java com bancos de dados hospedados em nuvem (AWS), contemplando o modelo relacional e NoSQL.

---

## 🗄️ Parte 1: Banco de Dados Relacional (PostgreSQL)

Esta etapa consiste no desenvolvimento de um programa em Java que realiza operações de inserção, leitura, atualização e deleção (CRUD), conectando-se a um banco de dados relacional hospedado na nuvem AWS

### 🚀 Tecnologias Utilizadas
* **Linguagem:** Java
* **SGBD:** PostgreSQL
* **Cloud/Infraestrutura:** AWS RDS
* **Driver JDBC:** `postgresql-42.7.13.jar`

### 💻 Como executar a aplicação
1. **Pré-requisitos:** Certifique-se de ter o JDK instalado em sua máquina e o driver JDBC no seu *classpath*.
2. **Configuração:** Abra o arquivo `CrudRelacional.java` e preencha as variáveis de conexão (`USER`, `PASSWORD`) com as credenciais configuradas na AWS RDS.
3. **Execução via Terminal:** Compile o arquivo usando `javac CrudRelacional.java` e depois execute com `java CrudRelacional`.
4. **Uso:** O programa apresentará um menu interativo no terminal. Basta seguir as instruções na tela para manipular as tabelas.

---

## 🍃 Parte 2: Banco de Dados NoSQL (MongoDB)

Esta etapa abrange a modelagem e implementação de um banco de dados NoSQL orientado a documentos. O objetivo foi realizar o mapeamento do modelo lógico relacional (desenvolvido na Parte 1) para o **MongoDB**, aplicando as mesmas restrições de integridade e desenvolvendo o CRUD da nova estrutura.

### 🚀 Tecnologias Utilizadas
* **Linguagem:** Java
* **SGBD NoSQL:** MongoDB
* **Cloud/Infraestrutura:** AWS (via MongoDB Atlas)
* **Driver:** `mongo-java-driver-3.12.14.jar`

### 💻 Como executar a aplicação
1. **Configuração:** Adicione o arquivo `mongo-java-driver-3.12.14.jar` contido neste repositório ao *Classpath* (Referenced Libraries) do seu projeto no VS Code ou IDE de preferência.
2. **Execução:** Compile e execute o arquivo `CrudNoSQL.java`.
3. **Uso:** Utilize o menu interativo no terminal para testar as operações de CRUD diretamente no servidor da AWS.

---
*Desenvolvido por Enzo Libório Fraga*
