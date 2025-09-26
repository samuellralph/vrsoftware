# VRS Pedidos GUI - Aplicação Desktop Java Swing

Uma aplicação desktop Java Swing para gerenciamento de pedidos que se comunica com um backend REST.

## Pré-requisitos

- Java 11 ou superior
- Maven 3.6 ou superior
- Backend REST rodando (opcional para teste da interface)

## Configuração

### URL do Backend
Por padrão, a aplicação se conecta em `http://localhost:8080/api/pedidos`.

Para alterar, modifique a constante `BASE_URL` em `PedidoService.java`:

```java
private static final String BASE_URL = "http://seu-backend:porta/api/pedidos";
```

## Execução

### Via Maven
```bash
mvn clean package
mvn exec:java -Dexec.mainClass="com.vrsoftware.pedidos.PedidosApplication"
```

### Via JAR
```bash
mvn clean package
java -jar target/vrs-pedidos-gui-1.0.0.jar
```

### Via Script Windows
```bash
run.bat
```

## Estrutura do Projeto

```
src/main/java/com/vrsoftware/pedidos/
├── PedidosApplication.java      # Classe principal
├── gui/
│   └── PedidosFrame.java        # Interface gráfica principal
├── model/
│   └── Pedido.java              # Modelo de dados
└── service/
    └── PedidoService.java       # Comunicação HTTP
```

## Desenvolvimento

Para contribuir com o projeto:

1. Clone o repositório
2. Configure seu IDE para Java 11+
3. Execute `mvn clean install` para baixar dependências
4. Use `mvn exec:java` para executar durante desenvolvimento