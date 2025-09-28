# VRS Pedidos - Sistema de Processamento Assíncrono

Sistema Spring Boot para processamento assíncrono de pedidos utilizando RabbitMQ.

## Tecnologias Utilizadas

- Spring Boot 3.2.1
- Spring AMQP (RabbitMQ)
- Spring Web
- Spring Validation
- Jackson (JSON)
- Docker & Docker Compose
- Java 17

## Estrutura do Projeto

```
src/main/java/com/vrsoftware/vrspedidos/
├── config/
│   └── RabbitMQConfig.java          # Configuração das filas e exchanges
├── controller/
│   └── PedidoController.java        # Endpoints REST
├── exception/
│   └── ExcecaoDeProcessamento.java  # Exceção customizada
├── model/
│   ├── Pedido.java                  # Modelo do pedido
│   ├── StatusPedido.java            # Modelo do status
│   └── StatusEnum.java              # Enum dos status
├── service/
│   ├── interfaces/
│   │   ├── IPedidoService.java           # Interface do serviço principal
│   │   ├── IPedidoConsumerService.java   # Interface do consumidor de pedidos
│   │   └── IStatusConsumerService.java   # Interface do consumidor de status
│   ├── PedidoService.java           # Implementação do serviço principal
│   ├── PedidoConsumerService.java   # Implementação do consumidor de pedidos
│   └── StatusConsumerService.java   # Implementação do consumidor de status
└── VrsPedidosApplication.java       # Classe principal
```

## Filas RabbitMQ

- **pedidos.entrada.vrs**: Fila de entrada para novos pedidos
- **pedidos.entrada.vrs.dlq**: Dead Letter Queue para pedidos com falha
- **pedidos.status.sucesso.vrs**: Fila para notificações de sucesso
- **pedidos.status.falha.vrs**: Fila para notificações de falha

## Como Executar

### 1. Iniciar RabbitMQ com Docker

```bash
docker-compose up -d
```

O RabbitMQ estará disponível em:
- **AMQP**: localhost:5672
- **Management UI**: http://localhost:15672 (guest/guest)

### 2. Executar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

## Monitoramento

- **RabbitMQ Management**: http://localhost:15672 para monitorar filas

## Exemplo de Teste

```bash
# Criar um pedido
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"produto": "Smartphone", "quantidade": 1}'

# Consultar status (substitua o ID retornado)
curl http://localhost:8080/api/pedidos/status/123e4567-e89b-12d3-a456-426614174000
```