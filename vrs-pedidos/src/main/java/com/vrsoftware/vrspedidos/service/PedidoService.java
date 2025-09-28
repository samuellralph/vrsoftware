package com.vrsoftware.vrspedidos.service;

import com.vrsoftware.vrspedidos.config.RabbitMQConfig;
import com.vrsoftware.vrspedidos.model.Pedido;
import com.vrsoftware.vrspedidos.model.StatusEnum;
import com.vrsoftware.vrspedidos.service.interfaces.IPedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PedidoService implements IPedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    
    private final RabbitTemplate rabbitTemplate;
    private final Map<UUID, String> statusPedidos = new ConcurrentHashMap<>();

    public PedidoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public UUID processarPedido(Pedido pedido) {
        logger.info("Processando pedido: {}", pedido);
        
        // Atualizar status em memória
        statusPedidos.put(pedido.getId(), StatusEnum.RECEBIDO.name());
        
        try {
            // Publicar pedido na fila
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_PEDIDOS,
                RabbitMQConfig.ROUTING_KEY_PEDIDOS,
                pedido
            );
            
            logger.info("Pedido {} enviado para fila com sucesso", pedido.getId());
            
            // Atualizar status para processando
            statusPedidos.put(pedido.getId(), StatusEnum.PROCESSANDO.name());
            
            return pedido.getId();
            
        } catch (Exception e) {
            logger.error("Erro ao enviar pedido {} para fila: {}", pedido.getId(), e.getMessage());
            statusPedidos.put(pedido.getId(), StatusEnum.FALHA.name());
            throw new RuntimeException("Erro ao processar pedido", e);
        }
    }

    public String obterStatusPedido(UUID idPedido) {
        return statusPedidos.getOrDefault(idPedido, "PEDIDO_NAO_ENCONTRADO");
    }

    public void atualizarStatusPedido(UUID idPedido, StatusEnum status) {
        statusPedidos.put(idPedido, status.name());
        logger.info("Status do pedido {} atualizado para: {}", idPedido, status);
    }
}