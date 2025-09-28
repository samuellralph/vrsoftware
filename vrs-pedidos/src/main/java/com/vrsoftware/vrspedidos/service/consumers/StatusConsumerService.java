package com.vrsoftware.vrspedidos.service.consumers;

import com.vrsoftware.vrspedidos.config.RabbitMQConfig;
import com.vrsoftware.vrspedidos.model.StatusPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class StatusConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(StatusConsumerService.class);

    @RabbitListener(queues = RabbitMQConfig.FILA_STATUS_SUCESSO, ackMode = "AUTO")
    public void processarStatusSucesso(StatusPedido statusPedido) {
        logger.info("Status de SUCESSO recebido para pedido {}: {}", 
                   statusPedido.getIdPedido(), statusPedido);
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_STATUS_FALHA, ackMode = "AUTO")
    public void processarStatusFalha(StatusPedido statusPedido) {
        logger.warn("Status de FALHA recebido para pedido {}: {} - Erro: {}", 
                   statusPedido.getIdPedido(), statusPedido, statusPedido.getMensagemErro());
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_PEDIDOS_DLQ, ackMode = "AUTO")
    public void processarPedidosDLQ(Object mensagem) {
        logger.error("Mensagem enviada para DLQ: {}", mensagem);
    }
}