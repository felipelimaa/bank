# Projeto API Bank
O projeto API Bank tem o objetivo de simular, de forma simples, transações bancárias. Esse projeto está sendo realizado como objeto de estudo e aprendizagem.

### Pré-requisitos
Antes de começar, você vai precisar ter instalado em sua máquina as seguintes ferramentas:
- OpenJDK 11 (https://adoptopenjdk.net/)
- Docker (https://docs.docker.com/engine/install/)
- Docker Compose (https://docs.docker.com/compose/install/)

### Features
**v1:**
- [X] Criar uma conta
- [X] Realizar deposito
- [X] Realizar saque
- [X] Realizar transferência
- [ ] Permitir a listagem de transações por conta

## Desenvolvimento
O projeto é executado através do _Spring Boot_ como framework backend e banco de dados _PostgreSQL_ para armazenamento dos dados.

Para execução do projeto, é necessário ter o banco de dados PostgreSQL instalado na máquina ou rodando em container.

Para executar o _PostgreSQL_ como container, utilize o seguinte comando:
```
docker-compose up -d postgresql-server
```

Assim, o servidor do banco de dados rodará localmente escutando a porta **5432**, com os seguintes dados de autenticação:

- **usuário:** bank
- **senha**: bank
- **conexão**: localhost:5432

Após o servidor do banco de dados estar em execução, abra o projeto na sua IDE desejada ou, caso deseje executar o ambiente baseado no código, execute o seguinte comando:

```
./gradlew bootRun
```

Através de uma IDE, é possível executar a classe: `BankApplication.groovy`

## Testes
Em breve

## Execução do projeto completo (build and deploy)
O projeto contempla um arquivo `docker-compose.yaml` e um `Dockerfile` composto com o processo de build e execução do projeto (multi-stage).

Caso deseje executar o projeto completo, com a build e ambiente de execução, bem como o servidor **PostgreSQL**, na pasta do projeto, execute o seguinte comando:

```
docker-compose up -d 
```

