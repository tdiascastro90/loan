# Loan Simulation API

Esta é uma API REST para simulação de empréstimos. A API permite calcular valores como o total a ser pago, parcelas mensais e juros totais, com base nos dados fornecidos.

## Pré-requisitos

- **Java 17** ou superior
- **Maven** (para gerenciamento de dependências e build)
- **Spring Boot**

## Configuração

### 1. Clone o repositório
```bash
git clone https://github.com/tdiascastro90/loan.git
```

### 2. Compilar e construir o projeto
Certifique-se de que o Maven está instalado corretamente e execute o seguinte comando:
```bash
mvn clean install
```

### 3. Executar a aplicação
Para iniciar a aplicação localmente:
```bash
mvn spring-boot:run
```

A aplicação será iniciada no endereço padrão:
```
http://localhost:8080
```

## Endpoints

### **POST /api/loan/simulate**

Simula o cálculo de empréstimos.

#### Requisição:
- **URL:** `/api/loan/simulate`
- **Método:** `POST`
- **Headers:**
    - `Content-Type: application/json`

#### Corpo da requisição:
Envie uma lista de objetos JSON no seguinte formato:
```json
[
  {
    "amount": 10000.0,
    "birthDate": "1990-01-01",
    "period": 12
  },
  {
    "amount": 15000.0,
    "birthDate": "1995-03-15",
    "period": 18
  }
]
```

- `amount`: Valor do empréstimo (deve ser maior que 0).
- `birthDate`: Data de nascimento no formato `yyyy-MM-dd`.
- `period`: Prazo de pagamento em meses (deve ser maior que 0).

#### Resposta:
- **Status:** `200 OK` (em caso de sucesso)
- **Corpo:**
```json
[
  {
    "totalPayment": 10500.0,
    "monthlyPayment": 875.0,
    "totalInterest": 500.0
  },
  {
    "totalPayment": 15900.0,
    "monthlyPayment": 883.33,
    "totalInterest": 900.0
  }
]
```

### Tratamento de erros
Se ocorrerem erros na validação, a API retornará:

#### Exemplo de erro (400 Bad Request):
```json
{
  "message": "Loan amount must be greater than 0."
}
```

### Erros comuns:
- `400 Bad Request`: Dados inválidos na requisição.
    - Exemplo: Montante negativo, período inválido ou formato de data incorreto.

## Testes

### Testes Unitários e de Integração
Os testes foram implementados utilizando **JUnit** e **RestAssured**. Para executar os testes, use o comando:
```bash
mvn test
```

Os testes incluem:
- Validações de entrada.
- Simulações de empréstimos.
- Cenários de erro.

## Kafka como Mensageria

### Motivos para Escolher Kafka

1. **Facilidade no Gerenciamento de Tópicos e Consumers**
  - Kafka oferece uma abstração simples baseada em **tópicos**, onde produtores escrevem mensagens e consumidores as leem.
  - O modelo de **grupos de consumidores** permite distribuir mensagens automaticamente entre os consumidores do mesmo grupo, simplificando o escalonamento horizontal.

2. **Alta Performance**
  - Kafka é projetado para **processamento em alta escala**, suportando milhares de mensagens por segundo com latência muito baixa.
  - O uso eficiente de disco como estrutura de logs imutáveis garante alta taxa de transferência.

3. **Durabilidade e Retenção**
  - Kafka persiste mensagens no disco e permite configurar políticas de retenção, como manter mensagens por um período fixo ou até um limite de espaço.
  - Isso proporciona **reprocessamento de mensagens** a qualquer momento.

4. **Escalabilidade Simples**
  - Kafka é fácil de escalar horizontalmente: basta adicionar partições ou consumidores.

5. **Amplo Suporte de Integração**
  - Kafka tem integração com diversas ferramentas e frameworks de Big Data, como Spark e Flink.

6. **Confiabilidade e Consistência**
  - Kafka suporta **replicação de partições**, garantindo alta disponibilidade e tolerância a falhas.

### Comparação com Outras Mensagerias

- **RabbitMQ**: Embora eficiente para filas simples, seu gerenciamento de roteamento e escalabilidade é mais complexo. Eu particularmente usei muito pouco, apenas para projetos pessoais bem pequenos.
- **Amazon SQS**: Oferece retenção limitada de mensagens e pode gerar custos significativos em alto tráfego.

### Conclusão
Kafka foi escolhido por ser altamente escalável, confiável, e pela facilidade de gerenciamento de tópicos e consumidores, tornando-o ideal para sistemas distribuídos de alta performance.


## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
- **Maven**
- **JUnit 5**
- **RestAssured**
- **Kafka**
- **SLF4J** para logging
- **Springdoc OpenAPI** (Swagger UI)
- **Mockito** para testes mockados
- **Docker e Docker Compose** para conteinerização

## Autor
Thiago Dias de Castro - tdiascastro90@gmail.com

