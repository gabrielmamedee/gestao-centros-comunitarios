# API RESTFul de Gestão de Centros Comunitários - PayStore

## Desafio Técnico para Engenheiro de Software - Phoebus

Este projeto consiste em uma API RESTful desenvolvida em **Java** com **Spring Boot** para gerenciar centros comunitários durante situações de emergência. A API permite o cadastro de centros, o controle de seus recursos e ocupação, e a realização de intercâmbios de recursos entre eles, ajudando na coordenação de esforços e na distribuição eficiente de ajuda.

### Principais Funcionalidades
- CRUD completo para Centros Comunitários.
- Atualização em tempo real da ocupação e cálculo do percentual.
- Sistema de intercâmbio de recursos baseado em pontos.
- Regra de negócio para flexibilizar trocas com centros superlotados (>90% de ocupação).
- Transações atômicas para garantir a consistência dos dados durante os intercâmbios.
- Geração de relatórios consolidados sobre Centros Comunitarios e Recursos.

## Tecnologias e Padrões Utilizados
- **Linguagem:** [Java 21](https://www.oracle.com/java/)
- **Framework:** [Spring Boot 3](https://spring.io/projects/spring-boot)
- **Banco de Dados:** [MongoDB](https://www.mongodb.com/)
- **Gerenciador de Dependências:** [Maven](https://maven.apache.org/)
- **Containerização:** [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- **Bibliotecas:**
    - `Spring Web`: Para Criar a API
    - `Spring Data MongoDB`: Facilita a integração com o MongoDB.
    - `Lombok`: Reduz código boilerplate (getters, setters, etc.).
    - `Springdoc OpenAPI`: Facilita a documentação da API (Swagger UI).
- **Arquitetura:**
    - **Arquitetura em Camadas (Layered Architecture):** O projeto é dividido em camadas de `Controller` (API), `Service` (regras de negócio) e `Repository` (acesso a dados), promovendo a separação de responsabilidades e a testabilidade.
    - **Padrão DTO (Data Transfer Object):** Utilizado para desacoplar a representação dos dados da API do modelo de persistência do banco de dados.
      <pre>
        └── com
            └── phoebus
                └── gestao_centros_comunitarios
                    ├── GestaoCentrosComunitariosApplication.java
                    ├── config          // configurações do Swagger
                    ├── controller      // Endpoints da API
                    ├── dto             // Objetos de Transferência de Dados
                    ├── enums           // Os tipos enumerados (ex: TipoRecurso)
                    ├── model           // Classes de domínio (entidades)
                    ├── repository      // Interfaces de acesso ao banco
                    └── service         // Lógica das regras de negocio
      </pre>

  
## Testes e Qualidade de Código

O projeto inclui uma suíte de testes unitários para garantir a qualidade e assegurar que as regras de negócio funcionem conforme o esperado.

- **Ferramentas**: Os testes foram desenvolvidos com **JUnit 5** e **Mockito**, já presentes na dependencia `spring-boot-starter-test`.
- **Escopo**: O foco dos testes está na camada de Serviço, especialmente no IntercambioService, que contém a lógica mais complexa do sistema (cálculo de pontos, validação de estoque e a regra de exceção para centros superlotados). Os testes validam tanto os cenários de sucesso quanto os de falha esperada.

## Instruções para Executar o Projeto

### 1. Pré-requisitos
- Java 21
- Apache Maven
- Docker e Docker Compose instalados
- Uma IDE de sua preferência (IntelliJ, VSCode, Eclipse), nesse projeto estou utilizando IntelliJ
- Um cliente de API como [Insomnia](https://insomnia.rest/) ou [Postman](https://www.postman.com/)

### 2. Instruções para Subir a Aplicação
- **2.1.** Clone o repositório para a sua máquina.
- **2.2.** Abra um terminal na pasta raiz do projeto (no mesmo nível do arquivo `docker-compose.yml`).
- **2.3.** Execute o comando abaixo para iniciar o container do MongoDB:
  ```bash
  docker-compose up -d
  ```
- **2.4.** Rode a aplicação:
  - No IntelliJ execute a classe "GestaoCentrosComunitariosApplication"

- A aplicação estará rodando em http://localhost:8080.

## Documentação (Swagger UI)

A API possui uma documentação gerada com Swagger. Após iniciar a aplicação, acesse no seu navegador:

http://localhost:8080/swagger-ui.html

Nesta página, você pode visualizar todos os endpoints.

## Endpoinst da API

A seguir estão exemplos de como consumir os principais endpoints usando cURL

---
### Centros Comunitários

**Cadastrar um novo Centro Comunitário**

Este endpoint registra um novo centro no sistema, incluindo seus dados básicos e o inventário inicial de recursos. O percentual de ocupação é calculado automaticamente no momento do cadastro.
```bash
  curl --location 'http://localhost:8080/api/v1/centros-comunitarios' \
  --header 'Content-Type: application/json' \
  --data '{
      "nome": "Centro Comunitário Central",
      "endereco": {
          "rua": "Rua Principal",
          "numero": "123",
          "cidade": "São Paulo",
          "estado": "SP",
          "cep": "01000-000"
      },
      "localizacao": "-46.6333, -23.5505",
      "capacidadeMaxima": 100,
      "ocupacaoAtual": 50,
      "recursos": [
          { "tipo": "MEDICO", "quantidade": 2 },
          { "tipo": "CESTA_BASICA", "quantidade": 150 }
    ]
  }'
```

**Listar todos os Centros Comunitários**

Retorna uma lista com todos os centros comunitários cadastrados no sistema, permitindo uma visão geral da rede de apoio.
```bash
  curl --location 'http://localhost:8080/api/v1/centros-comunitarios'
```

**Buscar um Centro por ID**

Retorna os dados detalhados de um único centro comunitário, identificado pelo seu ID.
```bash
  curl --location 'http://localhost:8080/api/v1/centros-comunitarios/ID_DO_CENTRO'
```

**Atualizar a Ocupação de um Centro**

Atualiza o número de pessoas abrigadas em um centro e recalcula o percentual de ocupação. Se a ocupação atingir 100%, uma notificação é gerada no console (simulando um evento para outro microsserviço, conforme solicitado nas descrições do desafio).
```bash
curl --location --request PATCH 'http://localhost:8080/api/v1/centros-comunitarios/ID_DO_CENTRO/ocupacao' \
--header 'Content-Type: application/json' \
--data '{
    "novaOcupacao": 95
}'
```
---
### Intercâmbio de Recursos

**Realizar um Intercâmbio**

Endpoint central da aplicação. Ele executa a troca de recursos entre dois centros, aplicando as seguintes regras de negócio:

- **Validação de Estoque**: Garante que ambos os centros possuem os recursos que estão oferecendo.

- **Cálculo de Pontos**: A soma dos pontos dos recursos oferecidos por cada centro é calculada com base em uma tabela fixa.

- **Regra de Troca**: Por padrão, a soma dos pontos dos dois lados deve ser igual.

- **Regra de Exceção**: Se um dos centros estiver com mais de 90% de ocupação, a troca é permitida mesmo com pontuações diferentes.

- **Atomicidade**: A operação inteira (subtração de recursos de um lado, adição no outro e registro do histórico) é atômica. Se qualquer passo falhar, toda a transação é revertida.
```bash
curl --location 'http://localhost:8080/api/v1/intercambios' \
--header 'Content-Type: application/json' \
--data '{
  "centroOrigemId": "ID_DO_CENTRO_A",
  "centroDestinoId": "ID_DO_CENTRO_B",
  "itensOferecidosOrigem": [
    { "tipoRecurso": "MEDICO", "quantidade": 1 }
  ],
  "itensOferecidosDestino": [
    { "tipoRecurso": "CESTA_BASICA", "quantidade": 3 }
  ]
}'
```
**Exemplo de um Response**
```json
{
  "id": "66981a8b3e3e0e1a1a1a1a1a",
  "centroOrigemId": "ID_DO_CENTRO_A",
  "centroDestinoId": "ID_DO_CENTRO_B",
  "itensOferecidosOrigem": [
    { "tipoRecurso": "MEDICO", "quantidade": 1 }
  ],
  "pontosOferecidosOrigem": 10,
  "itensOferecidosDestino": [
    { "tipoRecurso": "CESTA_BASICA", "quantidade": 3 }
  ],
  "pontosOferecidosDestino": 9,
  "dataIntercambio": "2025-07-17T18:30:03.123Z",
  "regraExcecaoAplicada": true
}
```
---
### Relatórios
**Centros com Ocupação > 90%**

Fornece uma lista com todos os centros que estão em estado crítico, ou seja, com sua capacidade máxima quase esgotada.
```bash
curl --location 'http://localhost:8080/api/v1/relatorios/ocupacao-alta'
```

**Média de Recursos por Centro**

Calcula a quantidade média de cada tipo de recurso disponível, considerando todos os centros da rede. Ajuda a identificar excessos ou carências de recursos em um nível macro.
```bash
curl --location 'http://localhost:8080/api/v1/relatorios/media-recursos'
```

**Histórico de Intercâmbios de um Centro**

Permite auditar as transações de um centro específico, retornando um histórico de todas as trocas em que ele esteve envolvido.

Parâmetros de Consulta:

- `centroId` (string, obrigatório): ID do centro para o qual o histórico será gerado.

- `desde` (data, opcional): Filtra as negociações a partir da data informada (formato: AAAA-MM-DD).

```bash
curl --location --request GET 'http://localhost:8080/api/v1/relatorios/historico-intercambios?centroId=ID_DO_CENTRO&desde=2025-07-01'
```