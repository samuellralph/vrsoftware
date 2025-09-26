package com.vrsoftware.vrspedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nomes das filas
    public static final String FILA_PEDIDOS_ENTRADA = "pedidos.entrada.vrs";
    public static final String FILA_STATUS_SUCESSO = "pedidos.status.sucesso.vrs";
    public static final String FILA_STATUS_FALHA = "pedidos.status.falha.vrs";
    public static final String FILA_PEDIDOS_DLQ = "pedidos.entrada.vrs.dlq";
    
    // Nomes dos exchanges
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String EXCHANGE_STATUS = "status.exchange";
    public static final String EXCHANGE_DLQ = "dlq.exchange";
    
    // Routing keys
    public static final String ROUTING_KEY_PEDIDOS = "pedidos.entrada";
    public static final String ROUTING_KEY_STATUS_SUCESSO = "status.sucesso";
    public static final String ROUTING_KEY_STATUS_FALHA = "status.falha";
    public static final String ROUTING_KEY_DLQ = "pedidos.dlq";

    // Configuração do conversor JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // Exchange para pedidos
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_PEDIDOS);
    }

    // Exchange para status
    @Bean
    public DirectExchange statusExchange() {
        return new DirectExchange(EXCHANGE_STATUS);
    }

    // Exchange para DLQ
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(EXCHANGE_DLQ);
    }

    // Fila de entrada de pedidos com DLQ configurada
    @Bean
    public Queue filaPedidosEntrada() {
        return QueueBuilder.durable(FILA_PEDIDOS_ENTRADA)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ)
                .build();
    }

    // Fila DLQ
    @Bean
    public Queue filaPedidosDLQ() {
        return QueueBuilder.durable(FILA_PEDIDOS_DLQ).build();
    }

    // Fila de status de sucesso
    @Bean
    public Queue filaStatusSucesso() {
        return QueueBuilder.durable(FILA_STATUS_SUCESSO).build();
    }

    // Fila de status de falha
    @Bean
    public Queue filaStatusFalha() {
        return QueueBuilder.durable(FILA_STATUS_FALHA).build();
    }

    // Bindings
    @Bean
    public Binding bindingPedidosEntrada() {
        return BindingBuilder
                .bind(filaPedidosEntrada())
                .to(pedidosExchange())
                .with(ROUTING_KEY_PEDIDOS);
    }

    @Bean
    public Binding bindingStatusSucesso() {
        return BindingBuilder
                .bind(filaStatusSucesso())
                .to(statusExchange())
                .with(ROUTING_KEY_STATUS_SUCESSO);
    }

    @Bean
    public Binding bindingStatusFalha() {
        return BindingBuilder
                .bind(filaStatusFalha())
                .to(statusExchange())
                .with(ROUTING_KEY_STATUS_FALHA);
    }

    @Bean
    public Binding bindingDLQ() {
        return BindingBuilder
                .bind(filaPedidosDLQ())
                .to(dlqExchange())
                .with(ROUTING_KEY_DLQ);
    }
}