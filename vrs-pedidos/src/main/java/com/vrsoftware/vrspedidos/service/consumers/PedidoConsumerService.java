package com.vrsoftware.vrspedidos.service.consumers;

import com.rabbitmq.client.Channel;
import com.vrsoftware.vrspedidos.config.RabbitMQConfig;
import com.vrsoftware.vrspedidos.exception.ExcecaoDeProcessamento;
import com.vrsoftware.vrspedidos.model.Pedido;
import com.vrsoftware.vrspedidos.model.StatusEnum;
import com.vrsoftware.vrspedidos.model.StatusPedido;
import com.vrsoftware.vrspedidos.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PedidoConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoConsumerService.class);

    private final RabbitTemplate rabbitTemplate;
    private final PedidoService pedidoService;
    private final Random random = new Random();

    public PedidoConsumerService(RabbitTemplate rabbitTemplate, PedidoService pedidoService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pedidoService = pedidoService;
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_PEDIDOS_ENTRADA, ackMode = "MANUAL")
    public void processarPedido(Pedido pedido, Message message, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        logger.info("Iniciando processamento do pedido: {}", pedido.getId());

        try {
            // Atualizar status para processando
            pedidoService.atualizarStatusPedido(pedido.getId(), StatusEnum.PROCESSANDO);

            // Simular tempo de processamento (1-3 segundos)
            int tempoProcessamento = 1000 + random.nextInt(2000); // 1000ms a 3000ms
            Thread.sleep(tempoProcessamento);

            // Simular falha com 20% de chance
            double chance = random.nextDouble();
            if (chance < 0.2) {
                throw new ExcecaoDeProcessamento("Falha simulada no processamento do pedido " + pedido.getId());
            }

            // Processamento bem-sucedido
            logger.info("Pedido {} processado com sucesso após {}ms", pedido.getId(), tempoProcessamento);

            // Atualizar status em memória
            pedidoService.atualizarStatusPedido(pedido.getId(), StatusEnum.SUCESSO);

            // Publicar status de sucesso
            StatusPedido statusSucesso = new StatusPedido(pedido.getId(), StatusEnum.SUCESSO);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_STATUS,
                    RabbitMQConfig.ROUTING_KEY_STATUS_SUCESSO,
                    statusSucesso
            );

            logger.info("Status de sucesso publicado para pedido: {}", pedido.getId());

            // Confirmar processamento da mensagem
            channel.basicAck(deliveryTag, false);

        } catch (ExcecaoDeProcessamento e) {
            logger.error("Falha no processamento do pedido {}: {}", pedido.getId(), e.getMessage());

            try {
                // Atualizar status em memória
                pedidoService.atualizarStatusPedido(pedido.getId(), StatusEnum.FALHA);

                // Publicar status de falha
                StatusPedido statusFalha = new StatusPedido(pedido.getId(), StatusEnum.FALHA, e.getMessage());
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_STATUS,
                        RabbitMQConfig.ROUTING_KEY_STATUS_FALHA,
                        statusFalha
                );

                logger.info("Status de falha publicado para pedido: {}", pedido.getId());

                // Rejeitar mensagem para enviar para DLQ
                channel.basicNack(deliveryTag, false, false);

            } catch (Exception ex) {
                logger.error("Erro ao processar falha do pedido {}: {}", pedido.getId(), ex.getMessage());
                try {
                    channel.basicNack(deliveryTag, false, false);
                } catch (Exception nackEx) {
                    logger.error("Erro ao fazer NACK da mensagem: {}", nackEx.getMessage());
                }
            }

        } catch (InterruptedException e) {
            logger.error("Processamento do pedido {} foi interrompido: {}", pedido.getId(), e.getMessage());
            Thread.currentThread().interrupt();

            try {
                pedidoService.atualizarStatusPedido(pedido.getId(), StatusEnum.FALHA);
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                logger.error("Erro ao processar interrupção: {}", ex.getMessage());
            }

        } catch (Exception e) {
            logger.error("Erro inesperado no processamento do pedido {}: {}", pedido.getId(), e.getMessage());

            try {
                pedidoService.atualizarStatusPedido(pedido.getId(), StatusEnum.FALHA);
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                logger.error("Erro ao processar exceção inesperada: {}", ex.getMessage());
            }
        }
    }
}