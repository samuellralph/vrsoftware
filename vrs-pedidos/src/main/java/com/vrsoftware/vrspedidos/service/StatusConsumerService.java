package com.vrsoftware.vrspedidos.service;

import com.vrsoftware.vrspedidos.config.RabbitMQConfig;
import com.vrsoftware.vrspedidos.model.StatusPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class StatusConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(StatusConsumerService.class);

    @RabbitListener(queues = RabbitMQConfig.FILA_STATUS_SUCESSO)
    public void processarStatusSucesso(StatusPedido statusPedido) {
        logger.info("Status de SUCESSO recebido para pedido {}: {}", 
                   statusPedido.getIdPedido(), statusPedido);
        
        // Aqui você poderia implementar lógica adicional como:
        // - Notificar sistemas externos
        // - Atualizar banco de dados
        // - Enviar emails de confirmação
        // - Integrar com sistemas de faturamento
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_STATUS_FALHA)
    public void processarStatusFalha(StatusPedido statusPedido) {
        logger.warn("Status de FALHA recebido para pedido {}: {} - Erro: {}", 
                   statusPedido.getIdPedido(), statusPedido, statusPedido.getMensagemErro());
        
        // Aqui você poderia implementar lógica adicional como:
        // - Alertar equipe de suporte
        // - Criar tickets de suporte
        // - Notificar cliente sobre o problema
        // - Implementar retry automático
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_PEDIDOS_DLQ)
    public void processarPedidosDLQ(Object mensagem) {
        logger.error("Mensagem enviada para DLQ: {}", mensagem);
        
        // Aqui você poderia implementar lógica para:
        // - Analisar mensagens que falharam
        // - Implementar reprocessamento manual
        // - Alertar administradores
        // - Armazenar para análise posterior
    }
}